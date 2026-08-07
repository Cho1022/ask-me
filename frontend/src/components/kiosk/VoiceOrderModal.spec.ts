import { flushPromises, mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { parseVoiceOrder, transcribeAudio } from '../../services/voiceApi'
import { useVoiceOrderStore } from '../../stores/voiceOrder'
import type { ParseVoiceOrderResponse } from '../../types/voice'
import VoiceOrderModal from './VoiceOrderModal.vue'

vi.mock('../../services/voiceApi', () => ({
  parseVoiceOrder: vi.fn(),
  transcribeAudio: vi.fn(),
}))

const parseVoiceOrderMock = vi.mocked(parseVoiceOrder)
const transcribeAudioMock = vi.mocked(transcribeAudio)
const originalMediaDevices = Object.getOwnPropertyDescriptor(navigator, 'mediaDevices')

function parsedOrder(transcript: string): ParseVoiceOrderResponse {
  return {
    transcript,
    normalizedTranscript: transcript,
    status: 'CONFIRMATION_REQUIRED',
    items: [],
    unresolvedTerms: [],
    message: '주문 내용을 확인해 주세요.',
  }
}

describe('VoiceOrderModal', () => {
  beforeEach(() => {
    parseVoiceOrderMock.mockReset()
    transcribeAudioMock.mockReset()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    if (originalMediaDevices) {
      Object.defineProperty(navigator, 'mediaDevices', originalMediaDevices)
    } else {
      Reflect.deleteProperty(navigator, 'mediaDevices')
    }
  })

  it('aborts an in-flight parse request when the modal closes', async () => {
    let requestSignal: AbortSignal | undefined
    parseVoiceOrderMock.mockImplementation((_transcript, signal) => {
      requestSignal = signal
      return new Promise((_resolve, reject) => {
        signal?.addEventListener('abort', () => {
          reject(Object.assign(new Error('aborted'), { name: 'AbortError' }))
        })
      })
    })
    const pinia = createPinia()
    const wrapper = mount(VoiceOrderModal, {
      props: { open: true },
      global: { plugins: [pinia] },
    })

    await wrapper.get('#manual-transcript').setValue('아이스 아메리카노 한 잔')
    await wrapper.get('.manual-transcript button').trigger('click')

    expect(requestSignal?.aborted).toBe(false)
    await wrapper.setProps({ open: false })
    await flushPromises()

    expect(requestSignal?.aborted).toBe(true)
    expect(useVoiceOrderStore(pinia).processing).toBe(false)
  })

  it('stops a stream that arrives after the modal closes', async () => {
    let resolveStream!: (stream: MediaStream) => void
    const streamPromise = new Promise<MediaStream>((resolve) => {
      resolveStream = resolve
    })
    const getUserMedia = vi.fn(() => streamPromise)
    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    })
    const mediaRecorderConstructor = vi.fn()
    class FakeMediaRecorder {
      static isTypeSupported() {
        return true
      }

      constructor() {
        mediaRecorderConstructor()
      }
    }
    vi.stubGlobal('MediaRecorder', FakeMediaRecorder)
    const stopTrack = vi.fn()
    const stream = { getTracks: () => [{ stop: stopTrack }] } as unknown as MediaStream
    const wrapper = mount(VoiceOrderModal, {
      props: { open: true },
      global: { plugins: [createPinia()] },
    })

    await wrapper.get('.record-button').trigger('click')
    expect(getUserMedia).toHaveBeenCalledOnce()
    await wrapper.setProps({ open: false })
    resolveStream(stream)
    await flushPromises()

    expect(stopTrack).toHaveBeenCalledOnce()
    expect(mediaRecorderConstructor).not.toHaveBeenCalled()
  })

  it('ignores a late response from a previous modal session', async () => {
    let resolveOldRequest!: (result: ParseVoiceOrderResponse) => void
    const oldRequest = new Promise<ParseVoiceOrderResponse>((resolve) => {
      resolveOldRequest = resolve
    })
    parseVoiceOrderMock
      .mockImplementationOnce(() => oldRequest)
      .mockResolvedValueOnce(parsedOrder('라떼 한 잔'))
    const pinia = createPinia()
    const wrapper = mount(VoiceOrderModal, {
      props: { open: true },
      global: { plugins: [pinia] },
    })

    await wrapper.get('#manual-transcript').setValue('아메리카노 한 잔')
    await wrapper.get('.manual-transcript button').trigger('click')
    await wrapper.setProps({ open: false })
    await wrapper.setProps({ open: true })
    await wrapper.get('#manual-transcript').setValue('라떼 한 잔')
    await wrapper.get('.manual-transcript button').trigger('click')
    await flushPromises()
    expect(useVoiceOrderStore(pinia).parsedOrder?.transcript).toBe('라떼 한 잔')

    resolveOldRequest(parsedOrder('아메리카노 한 잔'))
    await flushPromises()

    expect(useVoiceOrderStore(pinia).parsedOrder?.transcript).toBe('라떼 한 잔')
  })
})
