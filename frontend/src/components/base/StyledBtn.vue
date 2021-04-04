<script lang="ts">
import { defineComponent, PropType } from '@vue/composition-api'
import { QBtn } from 'quasar'
import { LooseDictionary } from 'quasar/dist/types/ts-helpers'

export default defineComponent<QBtn>({
  name: 'StyledBtn',
  extends: QBtn,
  props: {
    label: { type: String, default: undefined },
    outline: Boolean,
    icon: { type: String, default: undefined },
    stretch: Boolean,
    loading: Boolean,
    disable: Boolean,
    dense: Boolean,
    flat: Boolean,
    rounded: Boolean,
    round: Boolean,
    to: {
      type: [String, Object] as PropType<string | LooseDictionary>,
      default: undefined,
    },
    size: {
      type: String,
      default: '13px',
    },
    unelevated: {
      type: Boolean,
      default: true,
    },
    noCaps: {
      type: Boolean,
      default: true,
    },
    color: {
      type: String,
      default: 'primary',
    },
    textColor: {
      type: String,
      default: undefined,
    },
  },
})
</script>

<style lang="scss"></style>
