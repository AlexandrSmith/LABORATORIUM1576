<template>
  <q-dialog ref="dialogRef" no-backdrop-dismiss @hide="onDialogHide">
    <q-card class="q-dialog-plugin" style="width: 400px">
      <q-card-section class="flex justify-center">
        <div align="center" class="text-weight-bold q-mb-md">Новая предметная область</div>
        <styled-input v-model="value" :min="1" hide-bottom-space style="width: 350px" />
      </q-card-section>
      <q-card-actions class="flex justify-between q-mb-sm q-mx-md">
        <styled-btn outline style="width: 100px; margin-right: 10px" label="Отмена" @click="onCancelClick" />
        <styled-btn style="width: 100px" label="Сохранить" @click="onOKClick(value)" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import useDialog from 'components/common/useDialog'

export default defineComponent({
  name: 'NewSubjectDialog',
  props: {
    reduce: {
      type: Boolean,
      default: false,
    },
  },
  setup() {
    const dialog = useDialog()
    const value = ref('')

    return {
      ...dialog,
      value,
    }
  },
})
</script>

<style lang="scss" scoped></style>
