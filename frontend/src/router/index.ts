import { createRouter, createWebHistory } from 'vue-router'
import KioskView from '../views/KioskView.vue'
import OrderCompleteView from '../views/OrderCompleteView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'kiosk', component: KioskView },
    { path: '/complete/:orderNumber', name: 'complete', component: OrderCompleteView },
  ],
})
