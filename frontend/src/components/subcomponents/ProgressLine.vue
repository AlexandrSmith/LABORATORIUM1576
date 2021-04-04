<template>
  <div class="flex">
    <div v-for="i in numberOfStates" :key="i" class="flex">
      <div
        class="circle"
        :class="{
          'circle--previous': i < activeStep,
          'circle--done': i > activeStep && i <= maxStep,
          'circle--current': i === activeStep || (activeStep === 0 && i === 1),
          'circle--disable': i > maxStep && !(maxStep === 0 && i === 1),
        }"
        @click="i <= maxStep ? $emit('progress-stepped', i) : null"
      >
        <div class="flex justify-center column">{{ i }}</div>
      </div>
      <div v-if="i < numberOfStates" class="connector" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'

export default defineComponent({
  name: 'ProgressLine',
  components: {},
  props: {
    activeStep: {
      type: Number,
      default: 1,
    },
    maxStep: {
      type: Number,
      default: 1,
    },
    numberOfStates: {
      type: Number,
      default: 4,
    },
  },
  setup(props) {
    console.log(props.maxStep)
    return {}
  },
})
</script>

<style lang="scss" scoped>
.circle {
  display: flex;
  justify-content: center;
  border: 2px solid #ff6600;
  color: #ff6600;
  border-radius: 75%;
  width: 32px;
  height: 32px;
  cursor: pointer;

  &--current {
    background: #ff6600;
    color: white;
    border: 4px solid #ffa800;
  }

  &--previous {
    background: #ffa800;
    border: none;
    color: white;
  }

  &--done {
    background: #ffa800;
    border: none;
    color: white;
  }

  &--disable {
    cursor: not-allowed;
    background-color: white;
    color: #ff6600;
  }
}

.connector {
  margin: 15px 4px 0 4px;
  background-color: #ff6600;
  height: 4px;
  width: 32px;
}
</style>
