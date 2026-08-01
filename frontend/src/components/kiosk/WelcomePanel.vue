<script setup lang="ts">
defineEmits<{
  start: [channel: 'TOUCH' | 'VOICE']
}>()
</script>

<template>
  <section class="welcome-panel" aria-labelledby="welcome-title">
    <div class="welcome-copy">
      <p class="eyebrow">VOICE & TOUCH KIOSK</p>
      <h1 id="welcome-title">말해도, 눌러도<br />주문은 편안하게.</h1>
      <p class="welcome-description">
        메뉴를 천천히 살펴보거나 원하는 음료를 말해 주세요. 결제 전 주문 내용을 다시 확인할 수 있습니다.
      </p>
      <div class="welcome-promise" aria-label="이용 안내">
        <span>큰 글자</span><span>주문 확인</span><span>20초 음성 입력</span>
      </div>
    </div>

    <div class="start-actions">
      <button class="start-card start-card--touch" type="button" @click="$emit('start', 'TOUCH')">
        <span class="start-card__number">01</span>
        <span class="start-card__label">화면을 눌러 주문</span>
        <strong>터치 주문 시작</strong>
        <span class="start-card__hint">메뉴를 보고 하나씩 담아요</span>
      </button>
      <button class="start-card start-card--voice" type="button" @click="$emit('start', 'VOICE')">
        <span class="start-card__number">02</span>
        <span class="start-card__label">메뉴를 말해서 주문</span>
        <strong>음성 주문 시작</strong>
        <span class="start-card__hint">“아아 두 잔 주세요”처럼 말해요</span>
      </button>
    </div>
  </section>
</template>
