<template>
  <div id="q-app">
    <router-view />
  </div>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import { GlobalQuasarIconMapFn } from 'quasar'

const appIcons: Record<string, string> = {
  'app:logo': 'img:icons/favicon-96x96.png',

  'app:file': 'mdi-file-document-outline',
  'app:account': 'mdi-account-outline',
  'app:multiple': 'mdi-file-multiple-outline',
  'app:projects-open': 'mdi-folder-open-outline',
  'app:projects-close': 'mdi-folder-outline',
  'app:project': 'mdi-file-plus-outline',
  'app:menu': 'mdi-menu',
  'app:comment': 'mdi-comment-multiple-outline',
  'app:users': 'mdi-account-group-outline',
  'app:subjects': 'mdi-cog-outline',
  'app:edit': 'mdi-pencil-circle-outline',
  'app:download': 'mdi-download',
  'app:question': 'icons/question.png',
  'app:locked': 'mdi-lock-outline',
  'app:unlocked': 'mdi-lock-open-variant-outline',
  'app:share': 'mdi-eye-outline',
  'app:hide': 'mdi-eye-off-outline',
  'app:bookshelf': 'mdi-bookshelf',
  'app:sort': 'mdi-sort',
  'app:pin': 'mdi-pin-outline',
  'app:close': 'mdi-close',
  'app:save': 'mdi-content-save-outline',
  'app:delete': 'icons/plus-circle.svg',
  'app:accounts': 'mdi-account-supervisor-outline',
  // 'app:question': 'icons/question.svg',

  'app:microsoft': 'mdi-microsoft',

  'app:add': 'add',
  'app:plus': 'add',
  'app:plus-circle': 'mdi-plus-circle-outline',
  'app:minus-circle': 'mdi-minus-circle-outline',
  'app:attention': 'mdi-alert-circle',
  'app:filter': 'mdi-filter',
  'app:down': 'mdi-chevron-down',
  'app:up': 'mdi-chevron-up',
  'app:filter-outline': 'mdi-filter-outline',
  'app:sad': 'mdi-emoticon-sad-outline',
  'app:calendar': 'mdi-calendar',
  'app:search': 'search',
  'app:account-outline': 'mdi-account-outline',
  'app:favorite': 'mdi-heart',
  'app:favorite-outline': 'mdi-heart-outline',
}

const iconMapFn: GlobalQuasarIconMapFn = (iconName: string) => {
  if (iconName.startsWith('app:')) {
    const icon = appIcons[iconName]
    if (icon) {
      return { icon }
    }
    const name = iconName.substring(4)
    return {
      icon: `img:icons/${name}.png`,
    }
  }
}

export default defineComponent({
  name: 'App',
  setup(props, { root }) {
    root.$q.iconMapFn = iconMapFn
  },
})
</script>
