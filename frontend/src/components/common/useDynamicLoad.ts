import { onMounted, ref, Ref, watch } from '@vue/composition-api'
import { QVirtualScroll } from 'quasar'

export interface DynamicLoadFetchOptions {
  page: number
  [prop: string]: any
}

export interface DynamicLoadOptions {
  filter?: Ref<Record<string, unknown>>
  fetchData: (options: DynamicLoadFetchOptions) => Promise<number | void>
  tableData: Ref<unknown[]>
  pageSize: number
}

export default function useDynamicLoad(options: DynamicLoadOptions) {
  const { tableData, fetchData, filter, pageSize } = options

  const page = ref(0)
  const lastTo = ref(-1)
  const isLoading = ref(false)

  function resetPage() {
    page.value = 0
    lastTo.value = -1
  }

  function internalFetch(pageNo: number) {
    isLoading.value = true
    // console.log('internalFetch page', pageNo)
    const filterOptions = filter?.value ?? {}
    return fetchData({ ...filterOptions, page: pageNo }).finally(() => (isLoading.value = false))
  }

  function handleFetchResult(length: number | unknown, to?: number, scrollRef?: QVirtualScroll) {
    // console.log('page', page.value, 'length', length, 'to', to)
    if (typeof length !== 'number') {
      // console.log('length undefined')
      return
    }
    // случай когда вызываем не в onScroll
    if (to === undefined) {
      if (length < pageSize) {
        lastTo.value = length - 1
        // console.log('1 set lastTo', lastTo.value)
      }
    } else {
      if (length) {
        page.value += 1
        scrollRef?.refresh()
      }
      if (length < pageSize) {
        lastTo.value = to
        // console.log('2 set lastTo', lastTo.value)
      }
    }
  }

  onMounted(() => {
    if (filter !== undefined) {
      watch(
        () => filter.value,
        () => {
          resetPage()
          void internalFetch(0).then(handleFetchResult)
        },
        { immediate: true, deep: true }
      )
    } else {
      void internalFetch(0).then(handleFetchResult)
    }
  })

  function onScroll({ to, ref }: { to: number; ref: QVirtualScroll }) {
    const lastIndex = tableData.value.length - 1
    // console.log('onScroll to', to, 'lastIndex', lastIndex, 'lastTo', lastTo.value)
    if (!isLoading.value && to === lastIndex && lastTo.value !== to) {
      // console.log('need fetch')
      void internalFetch(page.value + 1).then((length) => handleFetchResult(length, to, ref))
    } else if (to < lastTo.value) {
      lastTo.value = -1
    }
  }

  return {
    isLoading,

    resetPage,
    onScroll,
  }
}
