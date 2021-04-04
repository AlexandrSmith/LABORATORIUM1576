<template>
  <div class="bordered-item">
    <div class="bordered-item__title">
      {{ title }}
    </div>
    <div v-if="isStudent" class="bordered-item__text">
      {{ 'Для продолжения работы свяжитесь с руководителем проекта' }}
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'

export default defineComponent({
  name: 'BorderedTextBlock',
  props: {
    title: String,
    isStudent: Boolean,
  },
  setup(props) {
    console.log('title: ', props.title)
    return {}
  },
})
</script>

<style lang="scss" scoped>
.bordered-item {
  margin: 10px 0;
  padding: 15px;
  background: #ffeaea;
  border: 1px solid #d1b19c;
  border-radius: 10px;

  &__title {
    text-align: center;
    font-weight: bold;
    font-size: 16px;
    color: #f86300;
  }

  &__text {
    text-align: center;
    font-weight: 300;
    font-size: 14px;
  }
}
</style>
