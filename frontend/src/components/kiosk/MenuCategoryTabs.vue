<script setup lang="ts">
interface Category {
  value: string
  label: string
  count: number
}

defineProps<{
  categories: Category[]
  active: string
}>()

defineEmits<{
  change: [category: string]
}>()
</script>

<template>
  <nav class="category-tabs" aria-label="메뉴 분류">
    <button
      v-for="category in categories"
      :key="category.value"
      type="button"
      class="category-tab"
      :class="{ 'category-tab--active': active === category.value }"
      :aria-pressed="active === category.value"
      @click="$emit('change', category.value)"
    >
      <span>{{ category.label }}</span>
      <small>{{ category.count }}</small>
    </button>
  </nav>
</template>
