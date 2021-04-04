<template>
  <div class="q-mb-lg">
    <!--    <div v-if="!isStudent">-->
    <!--      <div class="text-weight-bold q-mt-sm">{{ label }}</div>-->
    <!--      <div>{{ info }}</div>-->
    <!--    </div>-->
    <styled-input
      v-if="isStudent"
      ref="focusInput"
      v-model="value"
      :question-label="questionLabel"
      :tooltip="tooltip"
      hide-bottom-space
      type="textarea"
      :label="label"
      :required="required"
      :rows="4"
      @input="(value) => $emit('input', value)"
      @show-input="clearValue"
    />
    <bordered-text-block v-else-if="info" :label="label" :info="[info]" required bold :tooltip="tooltip" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from '@vue/composition-api'
import { QInput } from 'quasar'
import BorderedTextBlock from 'components/base/BorderedTextBlock.vue'
import * as R from 'ramda'

export default defineComponent({
  name: 'InfoBlock',
  components: {
    BorderedTextBlock,
  },
  props: {
    // value: String,
    tooltip: String,
    label: String,
    info: String,
    isStudent: Boolean,
    required: Boolean,
    questionLabel: Boolean,
  },
  setup(props, context) {
    const value = ref<string | undefined>(R.clone(props.info))
    const focusInput = ref<QInput | null>(null)

    watch(
      () => props.info,
      () => {
        value.value = R.clone(props.info)
      }
    )

    function clearValue(clear: boolean) {
      if (clear) {
        value.value = ''
        context.emit('input', value.value)
      }
    }

    function valid() {
      return !!focusInput.value?.validate()
    }

    return {
      value,
      focusInput,

      clearValue,
      valid,
    }
  },
})
</script>

<style lang="scss" scoped></style>
