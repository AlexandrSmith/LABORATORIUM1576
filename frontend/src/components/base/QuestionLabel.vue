<template>
  <div class="flex" style="font-size: 16px; margin: 0 4px">
    <q-checkbox v-if="!required" v-model="isFilledAndOpen" size="xs" color="black" @input="$emit('show-input')" />
    <span :class="{ 'text-weight-bold': bold }" class="flex column justify-center" style="margin-right: 2px">{{
      label
    }}</span>
    <div class="flex column justify-center">
      <q-img src="question.png" width="1rem" height="1rem">
        <q-tooltip v-if="tooltip">{{ tooltip }}</q-tooltip>
      </q-img>
      <!-- <q-icon name="app:question" style="size: 1rem; margin-bottom: 0; width: 24px; height: 24px" size="xs" >
        <q-tooltip v-if="tooltip">{{ tooltip }}</q-tooltip>
      </q-icon> -->
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from '@vue/composition-api'
import * as R from 'ramda'

export default defineComponent({
  name: 'QuestionLabel',
  components: {},
  props: {
    label: String,
    bold: Boolean,
    tooltip: {
      type: String,
      default: null,
    },
    required: Boolean,
    isFilled: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const isFilledAndOpen = ref<boolean>(R.clone(props.isFilled))

    watch(
      () => props.isFilled,
      () => {
        isFilledAndOpen.value = R.clone(props.isFilled)
      }
    )

    return {
      isFilledAndOpen,
    }
  },
})
</script>

<style lang="scss" scoped></style>
