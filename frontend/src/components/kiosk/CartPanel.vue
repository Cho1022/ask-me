<script setup lang="ts">
import type { CartItem, ServiceMode } from '../../types/order'

defineProps<{
  items: CartItem[]
  totalQuantity: number
  totalPrice: number
  serviceMode: ServiceMode
}>()

defineEmits<{
  changeQuantity: [key: string, delta: number]
  serviceMode: [mode: ServiceMode]
  clear: []
  checkout: []
}>()

function formatPrice(price: number) {
  return `${price.toLocaleString('ko-KR')}원`
}

function itemUnitPrice(item: CartItem) {
  const optionsPrice = item.menu.options
    .filter((option) => item.optionIds.includes(option.id))
    .reduce((sum, option) => sum + option.additionalPrice, 0)
  return item.menu.basePrice + (item.size === 'LARGE' ? 500 : 0) + optionsPrice
}

function optionLabel(item: CartItem) {
  const labels = item.menu.options
    .filter((option) => item.optionIds.includes(option.id))
    .map((option) => option.name)
  return [item.size === 'LARGE' ? '라지' : '기본', ...labels].join(' · ')
}
</script>

<template>
  <aside class="cart-panel" aria-labelledby="cart-title">
    <div class="receipt-edge" aria-hidden="true"></div>
    <header class="cart-panel__header">
      <div>
        <p class="eyebrow">ORDER TICKET</p>
        <h2 id="cart-title">내 주문</h2>
      </div>
      <button class="text-button" type="button" :disabled="!items.length" @click="$emit('clear')">모두 비우기</button>
    </header>

    <div class="service-mode" aria-label="식사 방식 선택">
      <button type="button" :class="{ active: serviceMode === 'DINE_IN' }" @click="$emit('serviceMode', 'DINE_IN')">
        매장에서 먹기
      </button>
      <button type="button" :class="{ active: serviceMode === 'TAKEOUT' }" @click="$emit('serviceMode', 'TAKEOUT')">
        포장하기
      </button>
    </div>

    <div v-if="items.length" class="cart-items">
      <article v-for="item in items" :key="item.key" class="cart-item">
        <div>
          <strong>{{ item.menu.name }}</strong>
          <p>{{ optionLabel(item) }}</p>
          <span>{{ formatPrice(itemUnitPrice(item)) }}</span>
        </div>
        <div class="quantity-control" :aria-label="`${item.menu.name} 수량`">
          <button type="button" @click="$emit('changeQuantity', item.key, -1)">−</button>
          <b>{{ item.quantity }}</b>
          <button type="button" :disabled="item.quantity >= 99" @click="$emit('changeQuantity', item.key, 1)">＋</button>
        </div>
      </article>
    </div>
    <div v-else class="empty-cart">
      <span aria-hidden="true">＋</span>
      <strong>아직 담은 메뉴가 없습니다</strong>
      <p>왼쪽 메뉴에서 원하는 음료를 골라 주세요.</p>
    </div>

    <footer class="cart-total">
      <div><span>총 {{ totalQuantity }}잔</span><strong>{{ formatPrice(totalPrice) }}</strong></div>
      <button type="button" :disabled="!items.length" @click="$emit('checkout')">주문 확인하기</button>
    </footer>
  </aside>
</template>
