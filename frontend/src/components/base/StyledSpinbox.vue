<template>
  <label class="flex column styled-spinbox no-wrap">
    <span :class="fieldClass">{{ fieldLabel }}</span>
    <q-input
      ref="focusInput"
      :value="valueForInputField"
      v-bind="$attrs"
      :rules="innerRules"
      :class="inputClass"
      :hide-bottom-space="hideBottomSpace"
      color="input"
      bg-color="input"
      :rows="rows"
      type="number"
      dense
      outlined
      bottom-slots
      :input-style="inputStyle"
      :min="min"
      :max="max"
      :step="step"
      :readonly="readonly || type === 'time'"
      @input="onInput"
    >
      <!--suppress JSUnusedLocalSymbols -->
      <slot v-for="(_, name) in $slots" :slot="name" :name="name" />
      <!--suppress JSUnusedLocalSymbols -->
      <template v-for="(_, name) in $scopedSlots" :slot="name" slot-scope="slotData">
        <slot :name="name" v-bind="slotData" />
      </template>
      <template v-if="!hideControls" #append>
        <div class="column no-wrap">
          <q-icon v-ripple name="mdi-chevron-up" class="cursor-pointer styled-spinbox__button" @click="upHandler" />
          <q-icon v-ripple name="mdi-chevron-down" class="cursor-pointer styled-spinbox__button" @click="downHandler" />
        </div>
      </template>
    </q-input>
  </label>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref } from '@vue/composition-api'
import { QInput } from 'quasar'
import { clamp } from 'ramda'
import requiredRule from 'src/utils/rules/requiredRule'

type FunctionRule = (val: any) => boolean | string

export default defineComponent({
  name: 'StyledSpinbox',
  props: {
    label: {
      type: String,
      default: '',
    },
    value: {
      type: Number,
      default: 0,
    },
    required: {
      type: Boolean,
      default: false,
    },
    rules: {
      type: Array as PropType<FunctionRule[]>,
      default: () => [],
    },
    rows: {
      type: Number,
      default: 0,
    },
    type: {
      type: String,
      default: '',
    },
    min: {
      type: Number,
      default: 0,
    },
    max: {
      type: Number,
      default: undefined,
    },
    step: {
      type: Number,
      default: 1,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    reserveLabelSpace: {
      type: Boolean,
      default: false,
    },
    hideBottomSpace: {
      type: Boolean,
      default: false,
    },
    hideControls: {
      type: Boolean,
      default: false,
    },
    align: {
      type: String as PropType<'left' | 'center' | 'right'>,
      default: 'left',
    },
  },
  setup(props, context) {
    const focusInput = ref<QInput | null>(null)
    const fieldLabel = computed(() => (props.label ? `${props.label}${props.required ? '*' : ''}` : ''))
    const fieldClass = computed(() => ({
      'styled-spinbox__label': true,
      'hidden': !props.label && !props.reserveLabelSpace,
      'invisible': !props.label && props.reserveLabelSpace,
      required: props.required,
    }))
    const inputClass = computed(() => ({
      'styled-spinbox__input': true,
      'col-grow': true,
      'styled-spinbox__buttons': !props.hideControls,
    }))

    const inputStyle = computed(() => ({
      textAlign: props.align,
    }))

    const innerRules = computed(() => {
      if (props.required) {
        return [requiredRule, ...props.rules]
      }
      return props.rules
    })

    const valueForInputField = computed(() => {
      return props.value
    })

    function focus() {
      focusInput.value?.focus()
    }

    function handleInput(value: string | number): number {
      const result = clamp(props.min, props.max ?? Infinity, +value)
      if (Number.isNaN(result) || !Number.isFinite(result)) {
        return props.min
      }
      return result
    }

    function onInput(value: string | number) {
      context.emit('input', handleInput(Math.round(+value * 100) / 100))
    }

    function downHandler() {
      if (props.value > props.min) onInput(+props.value - props.step)
    }

    function upHandler() {
      if (!props.max || props.value < props.max) onInput(+props.value + props.step)
    }

    return {
      fieldLabel,
      fieldClass,
      inputClass,
      inputStyle,
      innerRules,
      focusInput,
      valueForInputField,

      focus,
      downHandler,
      upHandler,
      onInput,
    }
  },
})
</script>

<style lang="scss">
.styled-spinbox {
  &__label {
    padding-bottom: 4px;
    min-height: 1rem;

    &:empty:before {
      content: '\200b';
    }

    &.required {
      font-weight: bold;
    }
  }

  &__input {
    .q-field__control {
      flex: 1 0 auto;
    }

    .q-field__control-container {
      > textarea {
        resize: none;
      }

      > input {
        color: #665339;
      }
    }
  }

  &__buttons {
    .q-field__control {
      padding-right: 0;
    }
  }

  &__button {
    box-sizing: border-box;
    background: #e0e0e0;
    border: 1px solid #bdbdbd;
    overflow: hidden;
    font-size: 20px;
    width: 24px;

    &:first-child {
      border-top-right-radius: 4px;
      border-bottom: 0;
    }

    &:last-child {
      border-bottom-right-radius: 4px;
    }
  }

  .q-field--outlined.q-field--readonly .q-field__control:before {
    border: 1px solid rgba(0, 0, 0, 0.24);
  }

  input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  input[type='number'] {
    -moz-appearance: textfield;
  }
}
</style>
