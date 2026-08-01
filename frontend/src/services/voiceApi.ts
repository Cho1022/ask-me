import type { ParseVoiceOrderResponse } from '../types/voice'
import { apiRequest } from './api'

export async function transcribeAudio(audio: Blob, durationMs: number): Promise<string> {
  const formData = new FormData()
  formData.append('audio', audio, 'voice-order.webm')
  formData.append('durationMs', String(durationMs))
  const result = await apiRequest<{ transcript: string }>('/api/voice/transcriptions', {
    method: 'POST',
    body: formData,
  })
  return result.transcript
}

export function parseVoiceOrder(transcript: string): Promise<ParseVoiceOrderResponse> {
  return apiRequest<ParseVoiceOrderResponse>('/api/voice/orders/parse', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ transcript }),
  })
}
