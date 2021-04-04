<template>
  <q-dialog ref="dialogRef" no-backdrop-dismiss @hide="onDialogHide" @click="onCancelClick">
    <q-img :src="url">
      <q-btn icon="app:close" style="position: absolute; top: 0; right: 0" size="xl" color="black" round outline />
    </q-img>
  </q-dialog>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import useDialog from 'components/common/useDialog'

export default defineComponent({
  name: 'ImagePopup',
  props: {
    url: { type: String, default: undefined },
  },
  setup() {
    const dialog = useDialog()

    return {
      ...dialog,
    }
  },
})
</script>

<style lang="scss" scoped></style>
