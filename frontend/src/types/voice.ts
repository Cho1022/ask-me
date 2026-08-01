import type { DrinkSize } from './order'

export type ParseStatus = 'CONFIRMATION_REQUIRED' | 'CLARIFICATION_REQUIRED' | 'NO_MATCH'
export type VoiceOrderAction = 'ADD' | 'REMOVE'

export interface ParsedOption {
  optionId: number
  code: string
  name: string
}

export interface ParsedOrderItem {
  menuId: number
  menuName: string
  quantity: number
  size: DrinkSize
  action: VoiceOrderAction
  options: ParsedOption[]
}

export interface ParseVoiceOrderResponse {
  transcript: string
  normalizedTranscript: string
  status: ParseStatus
  items: ParsedOrderItem[]
  unresolvedTerms: string[]
  message: string
}
