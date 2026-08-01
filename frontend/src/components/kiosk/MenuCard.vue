<script setup lang="ts">
import { computed, ref } from 'vue'
import type { Menu } from '../../types/menu'
import type { DrinkSize } from '../../types/order'

const props = defineProps<{ menu: Menu }>()
const emit = defineEmits<{
  add: [payload: { menu: Menu; size: DrinkSize; optionIds: number[] }]
}>()

const size = ref<DrinkSize>('REGULAR')
const optionIds = ref<number[]>([])
const displayedPrice = computed(() => {
  const optionPrice = props.menu.options
    .filter((option) => optionIds.value.includes(option.id))
    .reduce((sum, option) => sum + option.additionalPrice, 0)
  return props.menu.basePrice + (size.value === 'LARGE' ? 500 : 0) + optionPrice
})

function formatPrice(price: number) {
  return `${price.toLocaleString('ko-KR')}원`
}

function toggleOption(optionId: number) {
  optionIds.value = optionIds.value.includes(optionId)
    ? optionIds.value.filter((id) => id !== optionId)
    : [...optionIds.value, optionId]
}

function addToCart() {
  emit('add', { menu: props.menu, size: size.value, optionIds: optionIds.value })
}
</script>

<template>
  <article class="menu-card">
    <div class="menu-card__photo">
      <img :src="menu.imageUrl" :alt="`${menu.name} 메뉴 사진`" loading="lazy" />
      <span class="temperature-badge" :class="`temperature-badge--${menu.temperature.toLowerCase()}`">
        {{ menu.temperature === 'ICE' ? 'ICE' : menu.temperature === 'HOT' ? 'HOT' : '기본' }}
      </span>
    </div>

    <div class="menu-card__body">
      <p class="menu-card__category">{{ menu.category }}</p>
      <h3>{{ menu.name }}</h3>
      <p class="menu-card__description">{{ menu.description }}</p>

      <div class="size-toggle" aria-label="사이즈 선택">
        <button type="button" :class="{ active: size === 'REGULAR' }" @click="size = 'REGULAR'">기본</button>
        <button type="button" :class="{ active: size === 'LARGE' }" @click="size = 'LARGE'">라지 +500</button>
      </div>

      <div v-if="menu.options.length" class="option-list">
        <button
          v-for="option in menu.options"
          :key="option.id"
          type="button"
          class="option-chip"
          :class="{ active: optionIds.includes(option.id) }"
          :aria-pressed="optionIds.includes(option.id)"
          @click="toggleOption(option.id)"
        >
          {{ option.name }}<span v-if="option.additionalPrice"> +{{ option.additionalPrice }}</span>
        </button>
      </div>

      <button class="add-menu-button" type="button" @click="addToCart">
        <span>장바구니에 담기</span>
        <strong>{{ formatPrice(displayedPrice) }}</strong>
      </button>
    </div>
  </article>
</template>
