<template>
  <styled-input
    v-bind="$attrs"
    :value="value"
    :label="label"
    :mask="phonePattern"
    required-msg="Небходимо ввести номер телефона"
    :rules="internalRules"
    lazy-rules
    fill-mask
    type="tel"
    @input="onInput"
  />
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import phoneRule from 'src/utils/rules/phoneRule'

export const phonePattern = '+7 (###) ###-##-##'

export default defineComponent({
  name: 'PhoneInput',
  props: {
    value: {
      type: String,
      required: true,
    },
    label: {
      type: String,
      default: 'Телефон',
    },
    rules: {
      type: Array,
      default: () => [],
    },
  },
  setup(props, { emit }) {
    const internalRules = [phoneRule, ...props.rules]

    function onInput(value: string) {
      emit('input', value.replace(/[\s()-]/g, ''))
    }

    return {
      internalRules,
      phonePattern,
      onInput,
    }
  },
})
</script>

<style lang="scss" scoped></style>
