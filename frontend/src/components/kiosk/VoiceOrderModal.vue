<script setup lang="ts">
import { onBeforeUnmount, ref, watch } from 'vue'
import { parseVoiceOrder, transcribeAudio } from '../../services/voiceApi'
import { useVoiceOrderStore } from '../../stores/voiceOrder'
import type { ParseVoiceOrderResponse } from '../../types/voice'

const props = defineProps<{ open: boolean }>()
const emit = defineEmits<{
  close: []
  apply: [result: ParseVoiceOrderResponse]
}>()

const store = useVoiceOrderStore()
const manualTranscript = ref('')
const acquiring = ref(false)
let mediaRecorder: MediaRecorder | null = null
let mediaStream: MediaStream | null = null
let requestController: AbortController | null = null
let sessionVersion = 0
let chunks: BlobPart[] = []
let startedAt = 0
let stopTimer: number | null = null
let tickTimer: number | null = null

watch(
  () => props.open,
  (open) => {
    invalidateSession()
    if (open) {
      store.reset()
      manualTranscript.value = ''
    }
  },
  { immediate: true },
)

onBeforeUnmount(invalidateSession)

async function startRecording() {
  if (acquiring.value || store.recording || store.processing) return
  store.error = ''
  store.parsedOrder = null
  if (!navigator.mediaDevices?.getUserMedia || !window.MediaRecorder) {
    store.error = '이 브라우저는 음성 녹음을 지원하지 않습니다. 아래 입력창에 주문을 적어 주세요.'
    return
  }

  const mimeType = MediaRecorder.isTypeSupported('audio/webm;codecs=opus')
    ? 'audio/webm;codecs=opus'
    : MediaRecorder.isTypeSupported('audio/webm')
      ? 'audio/webm'
      : ''
  if (!mimeType) {
    store.error = 'webm/opus 녹음을 지원하는 Chrome 또는 Edge에서 이용해 주세요.'
    return
  }

  const session = sessionVersion
  acquiring.value = true
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    if (!isCurrentSession(session)) {
      stream.getTracks().forEach((track) => track.stop())
      return
    }
    mediaStream = stream
    chunks = []
    mediaRecorder = new MediaRecorder(mediaStream, { mimeType })
    mediaRecorder.ondataavailable = (event) => {
      if (event.data.size) chunks.push(event.data)
    }
    mediaRecorder.onstop = finishRecording
    startedAt = Date.now()
    store.recording = true
    store.elapsedMs = 0
    mediaRecorder.start(250)
    tickTimer = window.setInterval(() => {
      store.elapsedMs = Math.min(Date.now() - startedAt, 20_000)
    }, 100)
    stopTimer = window.setTimeout(stopRecording, 20_000)
  } catch {
    if (isCurrentSession(session)) {
      store.error = '마이크 권한을 허용한 뒤 다시 시도해 주세요.'
      stopAndCleanup()
    }
  } finally {
    if (session === sessionVersion) acquiring.value = false
  }
}

function stopRecording() {
  if (mediaRecorder?.state === 'recording') mediaRecorder.stop()
}

async function finishRecording() {
  const session = sessionVersion
  if (!isCurrentSession(session)) return
  const durationMs = Math.max(1, Math.min(Date.now() - startedAt, 20_000))
  const audio = new Blob(chunks, { type: mediaRecorder?.mimeType || 'audio/webm' })
  cleanupMedia()
  store.recording = false
  store.processing = true
  const controller = beginRequest()
  try {
    const transcript = await transcribeAudio(audio, durationMs, controller.signal)
    if (!isCurrentSession(session)) return
    manualTranscript.value = transcript
    const parsedOrder = await parseVoiceOrder(transcript, controller.signal)
    if (isCurrentSession(session)) store.parsedOrder = parsedOrder
  } catch (error) {
    if (isCurrentSession(session) && !isAbortError(error)) {
      store.error = error instanceof Error ? error.message : '음성을 처리하지 못했습니다.'
    }
  } finally {
    finishRequest(controller, session)
  }
}

async function parseManualTranscript() {
  if (acquiring.value || store.recording || store.processing) return
  const transcript = manualTranscript.value.trim()
  if (!transcript) {
    store.error = '주문 문장을 입력해 주세요.'
    return
  }
  const session = sessionVersion
  store.processing = true
  store.error = ''
  store.parsedOrder = null
  const controller = beginRequest()
  try {
    const parsedOrder = await parseVoiceOrder(transcript, controller.signal)
    if (isCurrentSession(session)) store.parsedOrder = parsedOrder
  } catch (error) {
    if (isCurrentSession(session) && !isAbortError(error)) {
      store.error = error instanceof Error ? error.message : '주문 문장을 해석하지 못했습니다.'
    }
  } finally {
    finishRequest(controller, session)
  }
}

function applyResult() {
  if (store.parsedOrder) emit('apply', store.parsedOrder)
}

function closeModal() {
  invalidateSession()
  emit('close')
}

function isCurrentSession(session: number): boolean {
  return props.open && session === sessionVersion
}

function beginRequest(): AbortController {
  requestController?.abort()
  requestController = new AbortController()
  return requestController
}

function finishRequest(controller: AbortController, session: number) {
  if (requestController === controller) requestController = null
  if (session === sessionVersion) store.processing = false
}

function isAbortError(error: unknown): boolean {
  return typeof error === 'object' && error !== null && 'name' in error && error.name === 'AbortError'
}

function invalidateSession() {
  sessionVersion += 1
  requestController?.abort()
  requestController = null
  acquiring.value = false
  store.processing = false
  stopAndCleanup()
}

function stopAndCleanup() {
  if (mediaRecorder) {
    mediaRecorder.onstop = null
    if (mediaRecorder.state === 'recording') mediaRecorder.stop()
  }
  cleanupMedia()
  store.recording = false
}

function cleanupMedia() {
  if (stopTimer !== null) window.clearTimeout(stopTimer)
  if (tickTimer !== null) window.clearInterval(tickTimer)
  stopTimer = null
  tickTimer = null
  mediaStream?.getTracks().forEach((track) => track.stop())
  mediaStream = null
  mediaRecorder = null
}
</script>

<template>
  <div v-if="open" class="modal-backdrop" role="presentation" @click.self="closeModal">
    <section class="voice-modal" role="dialog" aria-modal="true" aria-labelledby="voice-title">
      <header class="modal-header">
        <div>
          <p class="eyebrow">VOICE ORDER · 최대 20초</p>
          <h2 id="voice-title">원하는 메뉴를 말해 주세요</h2>
        </div>
        <button class="modal-close" type="button" aria-label="음성 주문 닫기" @click="closeModal">×</button>
      </header>

      <div class="voice-stage" :class="{ 'voice-stage--recording': store.recording }">
        <div class="voice-wave" aria-hidden="true">
          <i v-for="index in 14" :key="index" :style="{ '--bar': index }"></i>
        </div>
        <strong v-if="store.recording">듣고 있습니다 · {{ Math.ceil(store.elapsedMs / 1000) }}초</strong>
        <strong v-else-if="acquiring">마이크 권한을 확인하고 있습니다</strong>
        <strong v-else-if="store.processing">음성을 주문 문장으로 바꾸고 있습니다</strong>
        <strong v-else>버튼을 누르고 또박또박 말씀해 주세요</strong>
        <p>예: “아이스 아메리카노 두 잔하고 라떼 한 잔 주세요”</p>
        <button
          class="record-button"
          type="button"
          :disabled="acquiring || store.processing"
          @click="store.recording ? stopRecording() : startRecording()"
        >
          {{ acquiring ? '마이크 연결 중' : store.recording ? '말하기 끝내기' : '음성 녹음 시작' }}
        </button>
      </div>

      <div class="manual-transcript">
        <label for="manual-transcript">음성이 어려우면 주문 문장을 직접 적어도 됩니다</label>
        <div>
          <input
            id="manual-transcript"
            v-model="manualTranscript"
            type="text"
            placeholder="아아 두 잔하고 라떼 한 잔 주세요"
            :disabled="acquiring || store.recording || store.processing"
            @keyup.enter="parseManualTranscript"
          />
          <button
            type="button"
            :disabled="acquiring || store.recording || store.processing"
            @click="parseManualTranscript"
          >
            문장 확인
          </button>
        </div>
      </div>

      <p v-if="store.error" class="inline-error" role="alert">{{ store.error }}</p>

      <div v-if="store.parsedOrder" class="parsed-order">
        <div class="parsed-order__header">
          <div>
            <p class="eyebrow">RECOGNIZED ORDER</p>
            <strong>“{{ store.parsedOrder.transcript }}”</strong>
          </div>
          <span :class="`status-pill status-pill--${store.parsedOrder.status.toLowerCase()}`">
            {{ store.parsedOrder.status === 'CONFIRMATION_REQUIRED' ? '확인 필요' : '다시 확인' }}
          </span>
        </div>
        <ul v-if="store.parsedOrder.items.length">
          <li v-for="item in store.parsedOrder.items" :key="`${item.menuId}-${item.action}`">
            <span>{{ item.action === 'REMOVE' ? '빼기' : '담기' }}</span>
            <strong>{{ item.menuName }}</strong>
            <b>{{ item.quantity }}잔 · {{ item.size === 'LARGE' ? '라지' : '기본' }}</b>
          </li>
        </ul>
        <p v-if="store.parsedOrder.unresolvedTerms.length" class="unresolved-terms">
          다시 확인할 표현: {{ store.parsedOrder.unresolvedTerms.join(', ') }}
        </p>
        <p>{{ store.parsedOrder.message }}</p>
        <button
          class="primary-button"
          type="button"
          :disabled="!store.parsedOrder.items.length"
          @click="applyResult"
        >
          이 내용으로 장바구니 반영
        </button>
      </div>
    </section>
  </div>
</template>
