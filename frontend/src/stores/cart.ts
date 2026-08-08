import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { Menu } from '../types/menu'
import type { CartItem, DrinkSize, OrderChannel, ServiceMode } from '../types/order'

function cartKey(menuId: number, size: DrinkSize, optionIds: number[]): string {
  return `${menuId}:${size}:${[...optionIds].sort((a, b) => a - b).join(',')}`
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const orderChannel = ref<OrderChannel>('TOUCH')
  const serviceMode = ref<ServiceMode>('DINE_IN')
  const originalTranscript = ref('')

  const totalQuantity = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() =>
    items.value.reduce((sum, item) => {
      const optionsPrice = item.menu.options
        .filter((option) => item.optionIds.includes(option.id))
        .reduce((optionSum, option) => optionSum + option.additionalPrice, 0)
      const sizePrice = item.size === 'LARGE' ? 500 : 0
      return sum + (item.menu.basePrice + sizePrice + optionsPrice) * item.quantity
    }, 0),
  )

  function add(menu: Menu, size: DrinkSize = 'REGULAR', optionIds: number[] = [], quantity = 1) {
    const key = cartKey(menu.id, size, optionIds)
    const existing = items.value.find((item) => item.key === key)
    if (existing) {
      existing.quantity = Math.min(99, existing.quantity + quantity)
      return
    }
    items.value.push({ key, menu, size, optionIds: [...optionIds], quantity: Math.min(99, Math.max(1, quantity)) })
  }

  function remove(menuId: number, size: DrinkSize, optionIds: number[], quantity = 1): boolean {
    const key = cartKey(menuId, size, optionIds)
    const item = items.value.find((candidate) => candidate.key === key)
    if (!item) return false
    item.quantity -= quantity
    if (item.quantity <= 0) {
      items.value = items.value.filter((candidate) => candidate.key !== item.key)
    }
    return true
  }

  function changeQuantity(key: string, delta: number) {
    const item = items.value.find((candidate) => candidate.key === key)
    if (!item) return
    item.quantity = Math.min(99, item.quantity + delta)
    if (item.quantity <= 0) {
      items.value = items.value.filter((candidate) => candidate.key !== key)
    }
  }

  function clear() {
    items.value = []
    originalTranscript.value = ''
    orderChannel.value = 'TOUCH'
    serviceMode.value = 'DINE_IN'
  }

  return {
    items,
    orderChannel,
    serviceMode,
    originalTranscript,
    totalQuantity,
    totalPrice,
    add,
    remove,
    changeQuantity,
    clear,
  }
})
