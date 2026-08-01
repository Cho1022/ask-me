import type { Menu } from './menu'

export type DrinkSize = 'REGULAR' | 'LARGE'
export type OrderChannel = 'TOUCH' | 'VOICE'
export type ServiceMode = 'DINE_IN' | 'TAKEOUT'
export type PaymentMethod = 'CARD' | 'KAKAO_PAY' | 'PAYCO'

export interface CartItem {
  key: string
  menu: Menu
  quantity: number
  size: DrinkSize
  optionIds: number[]
}

export interface CreateOrderRequest {
  items: Array<{
    menuId: number
    quantity: number
    size: DrinkSize
    optionIds: number[]
  }>
  originalTranscript: string | null
  orderChannel: OrderChannel
  serviceMode: ServiceMode
  paymentMethod: PaymentMethod
}

export interface CreateOrderResponse {
  orderId: number
  orderNumber: string
  totalPrice: number
  createdAt: string
  message: string
}
