export type Temperature = 'ICE' | 'HOT' | 'NONE'
export type MenuOptionType = 'ADDITION' | 'EXCLUSION'

export interface MenuOption {
  id: number
  code: string
  name: string
  type: MenuOptionType
  additionalPrice: number
}

export interface Menu {
  id: number
  name: string
  groupName: string
  category: string
  description: string
  basePrice: number
  imageUrl: string
  temperature: Temperature
  options: MenuOption[]
}
