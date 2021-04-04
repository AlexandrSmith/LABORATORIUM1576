import { ref } from '@vue/composition-api'

export default function useIntersecting(once = true) {
  const isIntersecting = ref(false)

  function onIntersection(payload: IntersectionObserverEntry) {
    if (once) {
      if (payload.isIntersecting) {
        isIntersecting.value = payload.isIntersecting
      }
    } else {
      isIntersecting.value = payload.isIntersecting
    }
  }

  return {
    isIntersecting,
    onIntersection,
  }
}
