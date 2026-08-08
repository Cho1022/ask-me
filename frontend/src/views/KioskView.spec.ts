import { flushPromises, mount } from '@vue/test-utils'
import { createPinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import VoiceOrderModal from '../components/kiosk/VoiceOrderModal.vue'
import { fetchMenus } from '../services/menuApi'
import { useCartStore } from '../stores/cart'
import type { Menu } from '../types/menu'
import type { ParseVoiceOrderResponse } from '../types/voice'
import KioskView from './KioskView.vue'

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
}))

vi.mock('../services/menuApi', () => ({
  fetchMenus: vi.fn(),
}))

const fetchMenusMock = vi.mocked(fetchMenus)
const americano: Menu = {
  id: 1,
  name: '아이스 아메리카노',
  groupName: '아메리카노',
  category: 'coffee',
  description: '차갑고 깔끔한 커피',
  basePrice: 3000,
  imageUrl: 'https://example.com/americano.jpg',
  temperature: 'ICE',
  options: [{ id: 101, code: 'EXTRA_SHOT', name: '샷 추가', type: 'ADDITION', additionalPrice: 500 }],
}

describe('KioskView voice order application', () => {
  beforeEach(() => {
    fetchMenusMock.mockReset()
    fetchMenusMock.mockResolvedValue([americano])
  })

  it('keeps touch-order metadata when no exact cart item can be removed', async () => {
    const pinia = createPinia()
    const wrapper = mount(KioskView, { global: { plugins: [pinia] } })
    const cart = useCartStore(pinia)
    cart.add(americano, 'LARGE', [101])
    const removal: ParseVoiceOrderResponse = {
      transcript: '아이스 아메리카노 한 잔 빼줘',
      normalizedTranscript: '아이스 아메리카노 1 잔 빼줘',
      status: 'CONFIRMATION_REQUIRED',
      items: [
        {
          menuId: 1,
          menuName: '아이스 아메리카노',
          quantity: 1,
          size: 'REGULAR',
          action: 'REMOVE',
          options: [],
        },
      ],
      unresolvedTerms: [],
      message: '주문 내용을 확인해 주세요.',
    }
    await flushPromises()

    wrapper.findComponent(VoiceOrderModal).vm.$emit('apply', removal)
    await flushPromises()

    expect(cart.items).toHaveLength(1)
    expect(cart.orderChannel).toBe('TOUCH')
    expect(cart.originalTranscript).toBe('')
  })
})
