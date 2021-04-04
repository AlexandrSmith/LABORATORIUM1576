<template>
  <q-page class="column items-center justify-center">
    <login-form redirect />
  </q-page>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import LoginForm from 'components/auth/LoginForm.vue'

export default defineComponent({
  name: 'PageIndex',
  components: { LoginForm },
  setup() {
    return {}
  },
})
</script>

<style lang="scss" scoped></style>
