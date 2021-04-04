<template>
  <styled-select
    :label="label"
    :value="value"
    :loading="isLoading"
    use-input
    fill-input
    hide-selected
    :input-debounce="debounce"
    :options="displayedOptions"
    v-bind="$attrs"
    :popup-content-class="css['option-item']"
    hide-dropdown-icon
    type="textarea"
    @filter="filterFn"
    @input-value="$emit('input', $event)"
  />
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, useCSSModule } from '@vue/composition-api'
import { QInput, QMenu } from 'quasar'
import { sort } from 'ramda'
import mixedStringSort from 'src/utils/mixedStringSort'

type FilterFunction = (callback: () => void) => void

type FetchFunction = (query: string) => Promise<string[]>

export default defineComponent({
  name: 'Autocomplete',
  props: {
    label: {
      type: String,
      default: undefined,
    },
    requiredLabel: Boolean,
    required: Boolean,
    value: {
      type: String,
      default: '',
    },
    debounce: {
      type: [Number, String],
      default: 0,
    },
    options: {
      type: Array as PropType<string[]>,
      default: () => [],
    },
    fetchFn: {
      type: Function as PropType<FetchFunction | undefined>,
      default: undefined,
    },
  },
  setup(props) {
    const isLoading = ref(false)
    const innerOptions = ref<string[]>(props.options)
    const displayedOptions = computed(() => sort(mixedStringSort, innerOptions.value))
    const inputRef = ref<QInput | null>(null)
    const menuRef = ref<QMenu | null>(null)

    type CSS = { 'option-item': string } & Record<string, string>

    const css = useCSSModule() as CSS

    function filterFn(val: string, update: FilterFunction) {
      if (props.fetchFn) {
        if (val === '' || val == null) {
          update(() => {
            innerOptions.value = []
          })
        } else {
          isLoading.value = true
          props
            .fetchFn(val)
            .then((data) => {
              update(() => {
                innerOptions.value = data
              })
            })
            .finally(() => {
              isLoading.value = false
            })
        }
      } else {
        update(() => {
          const needle = val.toLocaleLowerCase()
          innerOptions.value = props.options.filter((v) => v != null && v.toLocaleLowerCase().indexOf(needle) > -1)
        })
      }
    }

    return {
      inputRef,
      menuRef,
      isLoading,
      displayedOptions,
      filterFn,
      css,
    }
  },
})
</script>

<style lang="scss" module>
.option-item {
  max-width: 420px;
}
</style>
