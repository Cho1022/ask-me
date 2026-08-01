<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import CartPanel from '../components/kiosk/CartPanel.vue'
import MenuCard from '../components/kiosk/MenuCard.vue'
import MenuCategoryTabs from '../components/kiosk/MenuCategoryTabs.vue'
import OrderConfirmModal from '../components/kiosk/OrderConfirmModal.vue'
import VoiceOrderModal from '../components/kiosk/VoiceOrderModal.vue'
import WelcomePanel from '../components/kiosk/WelcomePanel.vue'
import { fetchMenus } from '../services/menuApi'
import { createOrder } from '../services/orderApi'
import { useCartStore } from '../stores/cart'
import type { Menu } from '../types/menu'
import type { OrderChannel, PaymentMethod } from '../types/order'
import type { ParseVoiceOrderResponse } from '../types/voice'

const router = useRouter()
const cart = useCartStore()
const menus = ref<Menu[]>([])
const activeCategory = ref('all')
const searchQuery = ref('')
const started = ref(false)
const voiceOpen = ref(false)
const checkoutOpen = ref(false)
const loading = ref(true)
const pageError = ref('')
const notice = ref('메뉴를 골라 장바구니에 담아 주세요.')
const submitting = ref(false)
const checkoutError = ref('')

const categoryMeta = [
  { value: 'all', label: '전체 메뉴' },
  { value: 'coffee', label: '커피' },
  { value: 'beverage', label: '논커피' },
  { value: 'tea', label: '티' },
  { value: 'ade', label: '에이드' },
]

const categories = computed(() =>
  categoryMeta.map((category) => ({
    ...category,
    count:
      category.value === 'all'
        ? menus.value.length
        : menus.value.filter((menu) => menu.category === category.value).length,
  })),
)

const filteredMenus = computed(() => {
  const query = searchQuery.value.replaceAll(' ', '').toLowerCase()
  return menus.value.filter((menu) => {
    const categoryMatches = activeCategory.value === 'all' || menu.category === activeCategory.value
    const text = `${menu.name}${menu.groupName}${menu.description}`.replaceAll(' ', '').toLowerCase()
    return categoryMatches && (!query || text.includes(query))
  })
})

onMounted(async () => {
  try {
    menus.value = await fetchMenus()
  } catch (error) {
    pageError.value = error instanceof Error ? error.message : '메뉴를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
})

function startOrder(channel: OrderChannel) {
  cart.clear()
  cart.orderChannel = channel
  started.value = true
  if (channel === 'VOICE') voiceOpen.value = true
}

function addMenu(payload: { menu: Menu; size: 'REGULAR' | 'LARGE'; optionIds: number[] }) {
  cart.add(payload.menu, payload.size, payload.optionIds)
  notice.value = `${payload.menu.name}을 장바구니에 담았습니다.`
}

function applyVoiceOrder(result: ParseVoiceOrderResponse) {
  cart.orderChannel = 'VOICE'
  cart.originalTranscript = result.transcript
  let changed = 0
  for (const parsedItem of result.items) {
    const menu = menus.value.find((candidate) => candidate.id === parsedItem.menuId)
    if (!menu) continue
    if (parsedItem.action === 'REMOVE') {
      cart.remove(menu.id, parsedItem.quantity)
    } else {
      cart.add(
        menu,
        parsedItem.size,
        parsedItem.options.map((option) => option.optionId),
        parsedItem.quantity,
      )
    }
    changed += 1
  }
  notice.value = changed ? '음성 주문 내용을 장바구니에 반영했습니다.' : '반영할 메뉴가 없습니다.'
  if (changed) voiceOpen.value = false
}

async function submitOrder(paymentMethod: PaymentMethod) {
  submitting.value = true
  checkoutError.value = ''
  try {
    const response = await createOrder({
      items: cart.items.map((item) => ({
        menuId: item.menu.id,
        quantity: item.quantity,
        size: item.size,
        optionIds: item.optionIds,
      })),
      originalTranscript: cart.originalTranscript || null,
      orderChannel: cart.orderChannel,
      serviceMode: cart.serviceMode,
      paymentMethod,
    })
    const totalPrice = response.totalPrice
    cart.clear()
    await router.push({
      name: 'complete',
      params: { orderNumber: response.orderNumber },
      query: { total: String(totalPrice) },
    })
  } catch (error) {
    checkoutError.value = error instanceof Error ? error.message : '주문을 저장하지 못했습니다.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="kiosk-shell">
    <WelcomePanel v-if="!started" @start="startOrder" />

    <template v-else>
      <header class="kiosk-header">
        <button class="brand-mark" type="button" @click="started = false">
          <span>ASK<br />ME</span>
          <strong>편안한 음성 키오스크</strong>
        </button>
        <div class="header-status" aria-live="polite">
          <span :class="{ 'header-status__dot--voice': cart.orderChannel === 'VOICE' }"></span>
          {{ cart.orderChannel === 'VOICE' ? '음성 주문 모드' : '터치 주문 모드' }} · {{ notice }}
        </div>
        <button class="voice-shortcut" type="button" @click="voiceOpen = true">
          <span aria-hidden="true">●</span> 음성으로 메뉴 말하기
        </button>
      </header>

      <div class="kiosk-layout">
        <section class="menu-section" aria-labelledby="menu-title">
          <div class="menu-heading">
            <div>
              <p class="eyebrow">TODAY'S MENU</p>
              <h1 id="menu-title">오늘은 무엇을 드릴까요?</h1>
            </div>
            <label class="menu-search">
              <span class="sr-only">메뉴 검색</span>
              <input v-model="searchQuery" type="search" placeholder="메뉴 이름을 검색하세요" />
            </label>
          </div>

          <MenuCategoryTabs :categories="categories" :active="activeCategory" @change="activeCategory = $event" />

          <p v-if="pageError" class="inline-error" role="alert">{{ pageError }}</p>
          <div v-if="loading" class="loading-panel">메뉴를 준비하고 있습니다…</div>
          <div v-else-if="filteredMenus.length" class="menu-grid">
            <MenuCard v-for="menu in filteredMenus" :key="menu.id" :menu="menu" @add="addMenu" />
          </div>
          <div v-else class="loading-panel">조건에 맞는 메뉴가 없습니다. 검색어나 분류를 바꿔 주세요.</div>
        </section>

        <CartPanel
          :items="cart.items"
          :total-quantity="cart.totalQuantity"
          :total-price="cart.totalPrice"
          :service-mode="cart.serviceMode"
          @change-quantity="cart.changeQuantity"
          @service-mode="cart.serviceMode = $event"
          @clear="cart.clear"
          @checkout="checkoutOpen = true"
        />
      </div>
    </template>

    <VoiceOrderModal :open="voiceOpen" @close="voiceOpen = false" @apply="applyVoiceOrder" />
    <OrderConfirmModal
      :open="checkoutOpen"
      :items="cart.items"
      :total-price="cart.totalPrice"
      :service-mode="cart.serviceMode"
      :submitting="submitting"
      :error="checkoutError"
      @close="checkoutOpen = false"
      @confirm="submitOrder"
    />
  </main>
</template>
