<template>
  <styled-input
    v-model="internalValue"
    v-bind="$attrs"
    :reserve-label-space="reserveLabelSpace"
    :rules="internalRules"
    mask="##.##.####"
    placeholder="дд.мм.гггг"
  >
    <template #append>
      <q-icon name="event" class="cursor-pointer">
        <q-popup-proxy ref="qDateProxy" transition-show="scale" transition-hide="scale">
          <q-date v-model="internalValue" mask="DD.MM.YYYY" minimal :options="dateRange" @input="onPopupInput" />
        </q-popup-proxy>
      </q-icon>
    </template>
  </styled-input>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref } from '@vue/composition-api'
import StyledInput from 'components/base/StyledInput.vue'
import { format, isValid, parse } from 'date-fns'
import { QPopupProxy } from 'quasar'

export default defineComponent({
  name: 'DateInput',
  components: {
    StyledInput,
  },
  props: {
    value: {
      type: String,
      default: '',
    },
    rules: {
      type: Array,
      default: () => [],
    },
    min: {
      type: Date as PropType<Date>,
      default: undefined,
    },
    max: {
      type: Date as PropType<Date>,
      default: undefined,
    },
    reserveLabelSpace: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, context) {
    const localFormat = 'dd.MM.yyyy'
    const parseDate = (date: string) => parse(date, localFormat, new Date())
    const internalRules = [
      ...props.rules,
      (v: string) => v === '' || /^[0-3]\d\.[0-1]\d\.[\d]{4}$/.test(v) || 'Некорректная дата',
      (v: string) => v === '' || isValid(parseDate(v)) || 'Некорректная дата',
    ]

    const internalValue = computed({
      get: () => props.value,
      set: (value) => {
        context.emit('input', value)
      },
    })

    const qDateProxy = ref<QPopupProxy | null>(null)

    function onPopupInput() {
      console.log('onPopupInput', qDateProxy.value)
      qDateProxy.value?.hide()
    }

    const dateFormatString = 'yyyy/MM/dd'

    function formatDate(date?: Date): string | null {
      if (date) {
        return format(date, dateFormatString)
      }
      return null
    }

    function dateRange(date: string) {
      // const date = parse(dateStr, 'yyyy/MM/dd', new Date())
      const minDate = formatDate(props.min)
      const maxDate = formatDate(props.max)
      if (!!minDate && !!maxDate) {
        return minDate <= date && date <= maxDate
      } else if (!!minDate) {
        return minDate <= date
      } else if (!!maxDate) {
        return date <= maxDate
      }
      return true
    }

    return {
      internalRules,
      internalValue,
      qDateProxy,
      onPopupInput,
      dateRange,
    }
  },
})
</script>

<style lang="scss" scoped></style>
