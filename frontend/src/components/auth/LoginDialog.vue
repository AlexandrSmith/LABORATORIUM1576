<template>
  <q-dialog ref="dialogRef" no-backdrop-dismiss @hide="onDialogHide">
    <login-form
      :only-password="onlyPassword"
      :phone="phone"
      class="login-dialog"
      :class="{ 'only-password': onlyPassword }"
      client
      close
      @success="onOKClick"
      @close="onCancelClick"
    />
  </q-dialog>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import LoginForm from 'components/auth/LoginForm.vue'
import useDialog from 'components/common/useDialog'

export default defineComponent({
  name: 'LoginDialog',
  components: { LoginForm },
  props: {
    onlyPassword: Boolean,
    phone: { type: String, default: undefined },
  },
  setup() {
    const dialog = useDialog()

    return {
      ...dialog,
    }
  },
})
</script>

<style lang="scss" scoped>
.login-dialog {
  min-height: 400px;

  &.only-password {
    min-height: 350px;
  }
}
</style>
