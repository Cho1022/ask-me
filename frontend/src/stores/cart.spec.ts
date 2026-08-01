import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import type { Menu } from '../types/menu'
import { useCartStore } from './cart'

const americano: Menu = {
  id: 1,
  name: '아이스 아메리카노',
  groupName: '아메리카노',
  category: 'coffee',
  description: '차갑고 깔끔한 커피',
  basePrice: 3000,
  imageUrl: 'https://example.com/americano.jpg',
  temperature: 'ICE',
  options: [
    { id: 101, code: 'EXTRA_SHOT', name: '샷 추가', type: 'ADDITION', additionalPrice: 500 },
  ],
}

describe('cart store', () => {
  beforeEach(() => setActivePinia(createPinia()))

  it('merges identical menu configurations', () => {
    const cart = useCartStore()

    cart.add(americano, 'REGULAR', [], 2)
    cart.add(americano, 'REGULAR', [])

    expect(cart.items).toHaveLength(1)
    expect(cart.items[0]?.quantity).toBe(3)
    expect(cart.totalPrice).toBe(9000)
  })

  it('calculates size and option prices on the client preview', () => {
    const cart = useCartStore()

    cart.add(americano, 'LARGE', [101], 2)

    expect(cart.totalQuantity).toBe(2)
    expect(cart.totalPrice).toBe(8000)
  })

  it('removes voice-requested quantities without going below zero', () => {
    const cart = useCartStore()
    cart.add(americano, 'REGULAR', [], 2)

    cart.remove(americano.id, 2)

    expect(cart.items).toHaveLength(0)
  })

  it('caps a single configured item at 99 drinks', () => {
    const cart = useCartStore()

    cart.add(americano, 'REGULAR', [], 120)
    cart.changeQuantity(cart.items[0]!.key, 1)

    expect(cart.items[0]?.quantity).toBe(99)
  })
})
