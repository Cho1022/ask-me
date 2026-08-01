import type { CreateOrderRequest, CreateOrderResponse } from '../types/order'
import { apiRequest } from './api'

export function createOrder(payload: CreateOrderRequest): Promise<CreateOrderResponse> {
  return apiRequest<CreateOrderResponse>('/api/orders', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}
