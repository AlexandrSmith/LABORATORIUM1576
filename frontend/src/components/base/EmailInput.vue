<template>
  <styled-input
    v-bind="$attrs"
    :value="value"
    :label="label"
    required-msg="Небходимо ввести email"
    :rules="internalRules"
    lazy-rules
    type="email"
    @input="onInput"
  />
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import emailRule from 'src/utils/rules/emailRule'

export default defineComponent({
  name: 'EmailInput',
  props: {
    value: {
      type: String,
      required: true,
    },
    label: {
      type: String,
      default: 'Email',
    },
    rules: {
      type: Array,
      default: () => [],
    },
  },
  setup(props, { emit }) {
    const internalRules = [emailRule, ...props.rules]

    function onInput(value: string) {
      emit('input', value.replace(/[\s()-]/g, ''))
    }

    return {
      internalRules,
      onInput,
    }
  },
})
</script>

<style lang="scss" scoped></style>
