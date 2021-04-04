interface HorizontalData {
  left: number
  width: number
  scrollWidth: number

  hasPrev: boolean
  hasNext: boolean

  debounceId?: number | undefined
}

interface HorizontalInterface {
  refresh(data: HorizontalData): void
  scrollToLeft(left: number, behavior?: 'smooth' | 'auto'): void
  refreshAndScrollToBegin(): void
  scrollToIndex(i: number): void
}
