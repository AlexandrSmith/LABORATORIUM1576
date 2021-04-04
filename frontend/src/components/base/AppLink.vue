<template>
  <q-item
    v-bind="$attrs"
    :to="linkProps.to"
    :tag="linkProps.tag"
    :href="linkProps.href"
    :target="linkProps.target"
    :rel="linkProps.rel"
    clickable
  >
    <slot />
  </q-item>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  name: 'AppLink',
  props: {
    to: {
      type: String,
      required: true,
    },
  },
  computed: {
    linkProps() {
      const to = this.to
      if (/^(http(s)?|ftp):\/\//.exec(to)) {
        return {
          tag: 'a',
          href: to,
          target: '_blank',
          rel: 'noopener',
        }
      }
      return {
        to,
      }
    },
  },
})
</script>
