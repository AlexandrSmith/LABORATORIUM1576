import { onMounted, Ref, ref, SetupContext, watch } from '@vue/composition-api'

type WatcherSource<T> = Ref<T> | (() => T)

export interface DetectOverflow {
  isOverflowed: Ref<boolean>
}

export default function detectVerticalOverflow<T>(
  source: WatcherSource<T>,
  querySelector: string,
  ctx: SetupContext
): DetectOverflow {
  const isOverflowed = ref(false)

  function detectOverflow() {
    const element = ctx.root.$el.querySelector(querySelector)
    if (element) {
      const { scrollHeight, clientHeight } = element
      // console.log('table', scrollHeight, clientHeight);
      isOverflowed.value = scrollHeight > clientHeight
      // console.log('isOverflowed', isOverflowed.value)
    }
  }

  onMounted(() => {
    watch(source, detectOverflow)
    detectOverflow()
  })
  return { isOverflowed }
}
