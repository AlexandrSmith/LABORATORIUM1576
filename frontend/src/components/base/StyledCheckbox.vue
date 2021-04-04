<template>
  <label class="column styled-checkbox">
    <span :class="labelClasses">{{ label }}</span>
    <q-checkbox v-bind="$attrs" :value="value" keep-color v-on="$listeners" />
  </label>
</template>
<script lang="ts">
import { computed, defineComponent } from '@vue/composition-api'

export default defineComponent({
  name: 'StyledCheckbox',
  props: {
    label: {
      type: String,
      default: null,
    },
    value: {
      type: Boolean,
      required: true,
    },
    reserveLabelSpace: {
      type: Boolean,
      default: false,
    },
    right: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const labelClasses = computed(() => ({
      'styled-checkbox__label': true,
      'hidden': !props.label && !props.reserveLabelSpace,
      'invisible': !props.label && props.reserveLabelSpace,
      'right': props.right,
    }))
    return {
      labelClasses,
    }
  },
})
</script>

<style lang="scss" scoped>
.styled-checkbox {
  &__label {
    padding-bottom: 4px;

    &:empty:before {
      content: '\200b';
    }
  }
}

.q-checkbox {
  > &__inner {
  }
  > &__native {
    background-color: green;
  }
}

.q-checkbox__inner {
  background-color: green !important;
}
</style>
