<template>
  <label class="flex column styled-select">
    <span :class="fieldClass">{{ fieldLabel }}</span>
    <q-select
      :value="value"
      v-bind="$attrs"
      class="styled-select__input col-grow"
      bg-color="input"
      dense
      :class="{ 'required-border': required && value.length === 0 }"
      :outlined="outlined"
      bottom-slots
      :rules="innerRules"
      :options="options"
      :emit-value="emitValue"
      :map-options="mapOptions"
      :option-label="optionLabel"
      :option-value="optionValue"
      :options-dense="optionsDense"
      :option-disable="optionDisable"
      :placeholder="placeholder"
      dropdown-icon="mdi-chevron-down"
      v-on="$listeners"
    >
      <!--suppress JSUnusedLocalSymbols -->
      <slot v-for="(_, name) in $slots" :slot="name" :name="name" />
      <!--suppress JSUnusedLocalSymbols -->
      <template v-for="(_, name) in $scopedSlots" :slot="name" slot-scope="slotData">
        <slot :name="name" v-bind="slotData" />
      </template>
      <template v-if="value === ''" #selected>
        <div class="text-grey-7">{{ placeholder }}</div>
      </template>
    </q-select>
  </label>
</template>

<script lang="ts">
import { computed, defineComponent, PropType } from '@vue/composition-api'
import requiredRule from 'src/utils/rules/requiredRule'

type FunctionRule = (val: any) => boolean | string

export default defineComponent({
  name: 'StyledSelect',
  props: {
    label: { type: String, default: '' },
    value: {
      required: true,
      validator: (val: unknown) => {
        const type = typeof val
        return type === 'string' || type === 'number' || type === 'object'
      },
    },
    required: Boolean,
    requiredLabel: Boolean,
    rules: {
      type: Array as PropType<FunctionRule[]>,
      default: () => [],
    },
    options: { type: Array, required: true },
    optionValue: { type: [Function, String], default: undefined },
    optionLabel: { type: [Function, String], default: undefined },
    optionDisable: { type: [Function, String], default: undefined },
    optionsDense: Boolean,
    mapOptions: Boolean,
    emitValue: Boolean,
    placeholder: { type: String, default: '' },
    reserveLabelSpace: {
      type: Boolean,
      default: false,
    },
    outlined: {
      type: Boolean,
      default: true,
    },
    flat: Boolean,
  },
  setup(props) {
    const requiredLabel = computed(() => props.required || props.requiredLabel)
    const fieldLabel = computed(() => (props.label ? `${props.label}${requiredLabel.value ? '*' : ''}` : null))
    const fieldClass = computed(() => ({
      'styled-select__label': true,
      'hidden': !props.label && !props.reserveLabelSpace,
      'invisible': !props.label && props.reserveLabelSpace,
      'required': requiredLabel.value,
    }))

    const innerRules = computed(() => {
      if (props.required) {
        return [requiredRule, ...props.rules]
      }
      return props.rules
    })

    return {
      fieldLabel,
      fieldClass,
      innerRules,
    }
  },
})
</script>

<style lang="scss">
.styled-select {
  &__label {
    padding-bottom: 4px;

    &:empty:before {
      content: '\200b';
    }

    &.required {
      font-weight: bold;
    }
  }
}

.required-border {
  > div {
    &::after {
      position: absolute;
      top: -22px;
      right: 5px;
      font-style: italic;
      color: palevioletred;
      content: 'Обязательное поле*';
    }
  }
}
</style>
