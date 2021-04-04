<template>
  <styled-input
    ref="inputRef"
    :label="label"
    :sublabel="sublabel"
    :value="value"
    :debounce="debounce"
    :required-label="requiredLabel"
    :required="required"
    v-bind="$attrs"
    :loading="isLoading"
    @chip-delete="chipDeleteHandler"
    @input="onInput"
    @focus="onFocus"
    @keydown.up.prevent="moveOptionSelection(-1)"
    @keydown.down.prevent="moveOptionSelection(1)"
  >
    <q-menu
      ref="menuRef"
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
        <q-item
          v-for="(opt, i) in displayedOptions"
          :key="i"
          :focused="currentOption === i"
          clickable
          @click="
            () => {
              $emit('input', opt)
              $emit('input-click', opt, i)
            }
          "
        >
          <q-item-section>
            {{ opt }}
          </q-item-section>
        </q-item>
      </q-list>
    </q-menu>
  </styled-input>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, Ref, ref, unref, watch } from '@vue/composition-api'
import { QInput, QMenu } from 'quasar'
import { sort } from 'ramda'
import mixedStringSort from 'src/utils/mixedStringSort'

type FetchFunction = (query: string) => Promise<string[]>

export default defineComponent({
  name: 'AutocompleteV2',
  props: {
    label: {
      type: String,
      default: undefined,
    },
    sublabel: {
      type: Array as PropType<string[]>,
      default: () => [],
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
        const needle = val?.toLocaleLowerCase() ?? ''
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

    function onFocus() {
      void onInput(props.value)
    }

    function onShow() {
      inputRef.value?.focus()
    }

    const currentOption = ref(-1)
    const savedInput = ref(props.value)
    watch(
      () => currentOption.value,
      (value, oldValue) => {
        if (value === -1) {
          emitValue(savedInput)
          return
        } else if (oldValue === -1) {
          savedInput.value = props.value
        }
        emitValue(displayedOptions.value[currentOption.value])
      }
    )

    function resetCurrentOption() {
      currentOption.value = -1
    }

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

    // function onKey(payload: KeyboardEvent) {
    //   moveOptionSelection(payload.key === 'ArrowDown' ? 1 : -1)
    // }

    return {
      inputRef,
      menuRef,
      isLoading,
      displayedOptions,
      filterFn,
      onInput,
      chipDeleteHandler,
      onShow,
      onFocus,
      moveOptionSelection,
      currentOption,
    }
  },
})
</script>

<style lang="scss" module></style>
