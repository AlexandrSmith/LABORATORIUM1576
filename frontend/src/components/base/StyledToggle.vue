<template>
  <label class="flex justify-between">
    <span v-if="label" class="label">{{ label }}</span>
    <q-toggle :value="value" @input="$emit('input', $event)" />
  </label>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'

export default defineComponent({
  name: 'StyledToggle',
  components: {},
  props: {
    value: { type: Boolean },
    label: { type: String, default: null },
  },
})
</script>

<style lang="scss" scoped>
.label {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>
