<template>
  <div class="flex column styled-input">
    <div class="flex justify-between items-center">
      <question-label
        v-if="questionLabel"
        :label="fieldLabel"
        :required="required"
        :is-filled="value.length > 0"
        bold
        :tooltip="tooltip"
        @show-input="
          () => {
            showInput = !showInput
            $emit('show-input', showInput)
          }
        "
      />
      <span v-else :class="fieldClass">{{ fieldLabel }}</span>
      <q-icon v-if="deleteBtn" class="cursor-pointer" name="app:delete" @click="$emit('delete')" />
    </div>
    <div v-if="sublabel" class="row">
      <q-chip v-for="(chip, index) in sublabel" :key="index" removable square @remove="chipDeleteHandler(index)">
        {{ chip }}
      </q-chip>
    </div>
    <q-input
      v-if="showInput"
      ref="focusInput"
      :value="value"
      v-bind="$attrs"
      :rules="innerRules"
      :debounce="debounce"
      class="styled-input__input col-grow"
      bg-color="input"
      :rows="rows"
      dense
      outlined
      bottom-slots
      :class="{ 'required-border': required && value.length === 0 }"
      :lazy-rules="lazyRules"
      :mask="mask"
      :fill-mask="fillMask"
      :unmasked-value="unmaskedValue"
      :prefix="prefix"
      :suffix="suffix"
      v-on="$listeners"
    >
      <!--suppress JSUnusedLocalSymbols -->
      <slot v-for="(_, name) in $slots" :slot="name" :name="name" />
      <!--suppress JSUnusedLocalSymbols -->
      <template v-for="(_, name) in $scopedSlots" :slot="name" slot-scope="slotData">
        <slot :name="name" v-bind="slotData" />
      </template>
    </q-input>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from '@vue/composition-api'
import { QInput } from 'quasar'
import requiredRule from 'src/utils/rules/requiredRule'
import QuestionLabel from 'components/base/QuestionLabel.vue'

type FunctionRule = (val: any) => boolean | string

export default defineComponent({
  name: 'StyledInput',
  components: { QuestionLabel },
  // extends: QInput,
  props: {
    label: {
      type: String,
      default: '',
    },
    sublabel: {
      type: Array as PropType<string[]>,
      default: () => [],
    },
    value: {
      required: true,
      validator: (value: unknown) => value == null || typeof value === 'string' || typeof value === 'number',
    },
    debounce: {
      type: [String, Number],
      default: undefined,
    },
    required: {
      type: Boolean,
      default: false,
    },
    requiredLabel: {
      type: Boolean,
      default: false,
    },
    noRequiredDecorations: Boolean,
    deleteBtn: {
      type: Boolean,
      default: false,
    },
    requiredMsg: {
      type: String,
      default: 'Небходимо ввести значение',
    },
    rules: {
      type: Array as PropType<FunctionRule[]>,
      default: () => [],
    },
    lazyRules: {
      type: Boolean,
      default: true,
    },
    rows: {
      type: Number,
      default: 0,
    },
    reserveLabelSpace: {
      type: Boolean,
      default: false,
    },
    mask: {
      type: String,
      default: undefined,
    },
    fillMask: {
      type: [String, Boolean],
      default: false,
    },
    unmaskedValue: {
      type: Boolean,
      default: false,
    },
    prefix: {
      type: String,
      default: undefined,
    },
    suffix: {
      type: String,
      default: undefined,
    },
    questionLabel: Boolean,
    tooltip: {
      type: String,
      default: null,
    },
    bold: Boolean,
  },
  setup(props, context) {
    const focusInput = ref<QInput | null>(null)

    const requiredLabel = computed(() => (props.required || props.requiredLabel) && !props.noRequiredDecorations)

    const fieldLabel = computed(() => (props.label ? `${props.label}${props.requiredLabel ? '*' : ''}` : null))
    const fieldClass = computed(() => ({
      'styled-input__label': true,
      'hidden': !props.label && !props.reserveLabelSpace,
      'invisible': !props.label && props.reserveLabelSpace,
      'required': requiredLabel.value,
      'bold': props.bold,
      // 'required-border': props.required,
    }))

    const innerRules = computed(() => {
      if (props.required) {
        return [(x: any) => requiredRule(x, props.requiredMsg), ...props.rules]
      }
      return props.rules
    })

    function focus() {
      focusInput.value?.focus()
    }

    function valid() {
      return !!focusInput.value?.validate()
    }

    function chipDeleteHandler(index: number) {
      context.emit('chip-delete', index)
    }

    const showInput = ref(!(!props.value && !props.required && props.questionLabel))

    watch(
      () => props.value,
      () => {
        showInput.value = !(!props.value && !props.required && props.questionLabel)
      }
    )

    return {
      fieldLabel,
      fieldClass,
      innerRules,
      focusInput,
      showInput,

      focus,
      valid,
      chipDeleteHandler,
    }
  },
  // render(createElement, hack) {
  // }
})
</script>

<style lang="scss">
.styled-input {
  &__label {
    padding-bottom: 4px;

    /*&:empty:before {*/
    /*  content: '\200b';*/
    /*}*/

    &.required {
      font-weight: bold;
    }

    &.bold {
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
    // border: 1px solid palevioletred !important;
    // border-radius: 4px;
  }
  // color: palevioletred
}
</style>
