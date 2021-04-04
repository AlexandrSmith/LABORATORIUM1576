<template>
  <q-input
    ref="inputRef"
    type="search"
    class="search-input"
    input-class="search-input__input"
    :value="value"
    :debounce="debounce"
    :loading="isLoading"
    outlined
    dense
    clearable
    hide-bottom-space
    autocomplete="off"
    @input="onInput"
    @mousedown="() => onInput(value)"
    @keydown.up.prevent="moveOptionSelection(-1)"
    @keydown.down.prevent="moveOptionSelection(1)"
    @keydown.enter.prevent="onEnter"
  >
    <template #prepend>
      <q-icon name="app:search" />
    </template>
    <q-menu
      ref="menuRef"
      v-model="menuShowing"
      fit
      :offset="[0, 0]"
      auto-close
      no-focus
      no-parent-event
      no-refocus
      separate-close-popup
      @show="onShow"
    >
      <q-list>
        <div v-for="(opt, i) in displayedOptions" :key="i">
          <slot name="item" v-bind="{ item: opt, focused: currentOption === i }">
            <q-item clickable>
              <q-item-section>
                {{ opt }}
              </q-item-section>
            </q-item>
          </slot>
        </div>
      </q-list>
    </q-menu>
  </q-input>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, Ref, ref, unref, watch } from '@vue/composition-api'
import { QInput, QMenu } from 'quasar'
import { sort } from 'ramda'
import mixedStringSort from 'src/utils/mixedStringSort'

type FetchFunction = (query: string) => Promise<string[]>

export default defineComponent({
  name: 'SearchInput',
  props: {
    label: {
      type: String,
      default: undefined,
    },
    requiredLabel: Boolean,
    required: Boolean,
    value: {
      type: String,
      required: true,
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
  setup(props, context) {
    const isLoading = ref(false)
    const innerOptions = ref<string[]>(props.options)
    const displayedOptions = computed(() => sort(mixedStringSort, innerOptions.value))
    const inputRef = ref<QInput | null>(null)
    const menuRef = ref<QMenu | null>(null)
    const menuShowing = ref(false)

    function emitValue(input: string | Ref<string>) {
      context.emit('input', unref(input))
    }

    const { fetchFn } = props

    async function filterFn(val: string) {
      // console.log('filterFn', val)
      if (fetchFn) {
        if (val === '' || val == null) {
          innerOptions.value = []
        } else {
          isLoading.value = true
          innerOptions.value = await fetchFn(val).finally(() => {
            isLoading.value = false
          })
        }
      } else {
        const needle = val.toLocaleLowerCase()
        innerOptions.value = props.options.filter((v) => v != null && v.toLocaleLowerCase().indexOf(needle) > -1)
      }
    }

    async function onInput(input: string) {
      // console.log('onInput', input)
      emitValue(input)
      await filterFn(input)
      // console.log(displayedOptions.value.length)
      menuRef.value?.show()
      context.root.$nextTick(() => {
        menuRef.value?.updatePosition()
      })
    }

    function onShow() {
      inputRef.value?.focus()
    }

    const currentOption = ref(-1)

    function resetCurrentOption() {
      currentOption.value = -1
    }

    watch(() => props.value, resetCurrentOption)

    function moveOptionSelection(offset = 1) {
      const newIndex = currentOption.value + offset
      const lastIndex = displayedOptions.value.length - 1
      // console.log('newIndex', newIndex, 'lastIndex', lastIndex)
      if (!displayedOptions.value.length) {
        // resetCurrentOption()
        return
      }
      if (newIndex === -2) {
        // console.log('to tail')
        currentOption.value = lastIndex
      } else if (newIndex > lastIndex || newIndex < 0) {
        // console.log('out of bounds')
        resetCurrentOption()
      } else {
        // console.log('to specified index')
        currentOption.value = newIndex
      }
      // console.log('focusOnOption', currentOption.value)
    }

    function chipDeleteHandler(index: number) {
      context.emit('chip-delete', index)
    }

    function onEnter() {
      if (currentOption.value >= 0 && menuShowing.value) {
        context.emit('item-enter', displayedOptions.value[currentOption.value])
      } else {
        void onInput(props.value)
      }
    }

    // function onKey(payload: KeyboardEvent) {
    //   moveOptionSelection(payload.key === 'ArrowDown' ? 1 : -1)
    // }

    return {
      inputRef,
      menuRef,
      menuShowing,
      isLoading,
      displayedOptions,
      filterFn,
      onInput,
      chipDeleteHandler,
      onShow,
      onEnter,
      moveOptionSelection,
      currentOption,
    }
  },
})
</script>

<style lang="scss">
.search-input {
  & .q-field__control {
    padding: 0 8px;
  }

  & .q-field__control,
  & .q-field__prepend,
  & .q-field__append {
    height: unset;
  }

  & &__input {
    padding: 2px;
  }
}
</style>
