<template>
  <div class="relative-position q-my-md">
    <question-label
      v-if="!withoutQuestion"
      class="absolute question-label q-ml-sm"
      :class="{ 'question-label--done': done }"
      :label="label"
      :required="required"
      bold
      :tooltip="tooltip"
    />
    <div v-for="i in info" :key="i" class="q-pa-md bordered-item" :class="{ 'bordered-item--done': done }">
      {{ i }}
    </div>
    <div v-if="$slots.title" class="q-pa-md bordered-item" :class="{ 'bordered-item--done': done }">
      <slot name="title" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from '@vue/composition-api'
import QuestionLabel from 'components/base/QuestionLabel.vue'

export default defineComponent({
  name: 'BorderedTextBlock',
  components: { QuestionLabel },
  props: {
    label: String,
    done: Boolean,
    info: {
      type: Array as PropType<string[]>,
      default: () => [],
    },
    required: Boolean,
    withoutQuestion: Boolean,
    bold: Boolean,
    tooltip: String,
  },
  setup() {
    return {}
  },
})
</script>

<style lang="scss" scoped>
.bordered-item {
  border: 1px solid #bdbdbd;
  box-sizing: border-box;
  border-radius: 10px;

  &--done {
    background: #fff7ea;
    border: 1px solid #d1b19c;
  }
}

.question-label {
  top: -13px;
  left: 13px;
  z-index: 1;

  &::before {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 45%;
    z-index: -1;
    background: white;
  }

  &--done {
    &::before {
      background: #fff7ea !important;
    }
  }
}
</style>
