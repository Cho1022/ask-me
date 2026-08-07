import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { ParseVoiceOrderResponse } from '../types/voice'

export const useVoiceOrderStore = defineStore('voiceOrder', () => {
  const recording = ref(false)
  const processing = ref(false)
  const elapsedMs = ref(0)
  const parsedOrder = ref<ParseVoiceOrderResponse | null>(null)
  const error = ref('')

  function reset() {
    recording.value = false
    processing.value = false
    elapsedMs.value = 0
    parsedOrder.value = null
    error.value = ''
  }

  return { recording, processing, elapsedMs, parsedOrder, error, reset }
})
