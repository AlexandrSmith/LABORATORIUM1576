<template>
  <q-layout view="lHr LpR lfr">
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'

export default defineComponent({
  name: 'LoginLayout',
})
</script>

<style lang="scss" scoped></style>
