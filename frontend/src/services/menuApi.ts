import type { Menu } from '../types/menu'
import { apiRequest } from './api'

export function fetchMenus(): Promise<Menu[]> {
  return apiRequest<Menu[]>('/api/menus')
}
