<script setup lang="ts">
import { ref, watch } from 'vue'
import type { CartItem, PaymentMethod, ServiceMode } from '../../types/order'

const props = defineProps<{
  open: boolean
  items: CartItem[]
  totalPrice: number
  serviceMode: ServiceMode
  submitting: boolean
  error: string
}>()

const emit = defineEmits<{
  close: []
  confirm: [paymentMethod: PaymentMethod]
}>()

const paymentMethod = ref<PaymentMethod>('CARD')
watch(
  () => props.open,
  (open) => {
    if (open) paymentMethod.value = 'CARD'
  },
)

const methods: Array<{ value: PaymentMethod; label: string; description: string }> = [
  { value: 'CARD', label: '신용·체크카드', description: '카드를 단말기에 꽂아 주세요' },
  { value: 'KAKAO_PAY', label: '카카오페이', description: 'QR 또는 바코드로 결제해요' },
  { value: 'PAYCO', label: 'PAYCO', description: 'PAYCO 바코드를 준비해 주세요' },
]

function formatPrice(price: number) {
  return `${price.toLocaleString('ko-KR')}원`
}
</script>

<template>
  <div v-if="open" class="modal-backdrop" role="presentation" @click.self="$emit('close')">
    <section class="checkout-modal" role="dialog" aria-modal="true" aria-labelledby="checkout-title">
      <header class="modal-header">
        <div>
          <p class="eyebrow">FINAL CHECK</p>
          <h2 id="checkout-title">주문을 마지막으로 확인해 주세요</h2>
        </div>
        <button class="modal-close" type="button" aria-label="주문 확인 닫기" @click="$emit('close')">×</button>
      </header>

      <div class="checkout-summary">
        <span>{{ serviceMode === 'DINE_IN' ? '매장에서 먹기' : '포장하기' }}</span>
        <strong>{{ items.length }}개 메뉴 · {{ formatPrice(totalPrice) }}</strong>
      </div>

      <div class="payment-methods" role="radiogroup" aria-label="결제 수단">
        <button
          v-for="method in methods"
          :key="method.value"
          type="button"
          role="radio"
          :aria-checked="paymentMethod === method.value"
          :class="{ active: paymentMethod === method.value }"
          @click="paymentMethod = method.value"
        >
          <span>{{ method.label }}</span>
          <small>{{ method.description }}</small>
        </button>
      </div>

      <p class="checkout-notice">결제 버튼을 누른 뒤 서버가 현재 메뉴 가격을 다시 계산하고 주문을 저장합니다.</p>
      <p v-if="error" class="inline-error" role="alert">{{ error }}</p>
      <div class="modal-actions">
        <button class="secondary-button" type="button" :disabled="submitting" @click="$emit('close')">메뉴로 돌아가기</button>
        <button class="primary-button" type="button" :disabled="submitting" @click="$emit('confirm', paymentMethod)">
          {{ submitting ? '주문을 저장하는 중…' : `${formatPrice(totalPrice)} 결제하기` }}
        </button>
      </div>
    </section>
  </div>
</template>
