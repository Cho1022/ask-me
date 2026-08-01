<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const orderNumber = computed(() => String(route.params.orderNumber ?? ''))
const totalPrice = computed(() => Number(route.query.total ?? 0))
let returnTimer: number | null = null

onMounted(() => {
  returnTimer = window.setTimeout(() => router.push('/'), 8_000)
})

onUnmounted(() => {
  if (returnTimer !== null) window.clearTimeout(returnTimer)
})
</script>

<template>
  <main class="complete-view">
    <section class="complete-ticket">
      <p class="eyebrow">ORDER COMPLETE</p>
      <div class="complete-check" aria-hidden="true">✓</div>
      <h1>주문이 완료되었습니다</h1>
      <p>주문 번호가 불리면 픽업대에서 메뉴를 받아 주세요.</p>
      <div class="order-number">
        <span>주문 번호</span>
        <strong>{{ orderNumber }}</strong>
        <small>{{ totalPrice.toLocaleString('ko-KR') }}원 결제 요청</small>
      </div>
      <button class="primary-button" type="button" @click="router.push('/')">처음 화면으로 돌아가기</button>
      <p class="auto-return">8초 후 자동으로 처음 화면으로 돌아갑니다.</p>
    </section>
  </main>
</template>
