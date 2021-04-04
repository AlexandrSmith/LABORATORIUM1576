import { useCSSModule } from '@vue/composition-api'

export function getCssModule<T extends Record<string, string>>(): T {
  return useCSSModule() as T
}

// type v1 = { [K in typeof arr[number]]: string }
// return {} as v1

export function useCssModule<T extends string>(classes: readonly T[]): { [K in typeof classes[number]]: string } {
  return useCSSModule() as { [K in typeof classes[number]]: string }
}
