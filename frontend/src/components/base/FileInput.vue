<template>
  <div class="flex column q-my-sm q-mr-sm">
    <div
      class="flex justify-center img-block"
      style="position: relative; perspective: 100px"
      @mouseenter="hovered = true"
      @mouseleave="hovered = false"
    >
      <q-img
        v-if="isImage"
        class="img-block__image"
        :src="file.url"
        style="height: 4rem; width: 4rem; cursor: pointer"
        @click="openImageHandler(file)"
      />
      <q-icon v-else name="app:file" style="font-size: 4rem; cursor: pointer" @click="downloadHandler(file)" />
      <q-btn
        v-if="hovered"
        icon="app:download"
        class="action-btn"
        flat
        round
        size="xs"
        style="top: 0; left: 0"
        @click="downloadHandler(file)"
      />
      <div v-if="!read && hovered" class="flex column justify-between">
        <q-btn
          icon="app:close"
          class="action-btn"
          flat
          round
          style="top: 0; right: 0"
          size="xs"
          @click="$emit('delete', file)"
        />
        <q-btn
          icon="app:pin"
          class="action-btn"
          flat
          round
          size="xs"
          style="bottom: 0; right: 0"
          :style="file.pinned ? 'background-color: #2F80ED; color: white' : 'background-color: black; color: white'"
          @click="$emit('pin', file)"
        />
        <!-- <q-img style="bottom: 0; right: 0" src="app:pin" size="24px" /> -->
      </div>
    </div>
    <div class="ellipsis" style="max-width: 100px; overflow: hidden; height: 20px">{{ file.filename }}</div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref } from '@vue/composition-api'
import { Dialog } from 'quasar'
import { FileInfo } from 'src/models/Project'
import { downloadHandler } from 'src/utils/downloadLink'
import ImagePopup from 'components/base/ImagePopup.vue'

export default defineComponent({
  name: 'FileInput',
  components: {},
  props: {
    file: {
      type: Object as PropType<FileInfo>,
      default: null,
    },
    read: Boolean,
  },
  setup(props) {
    const hovered = ref(false)
    const isImage = computed(() => {
      if (!props.file) return false

      const splittedFileName = props.file.filename.split('.')
      const length = splittedFileName.length - 1
      if (length >= 0) if (['svg', 'png', 'jpg', 'jped'].includes(splittedFileName[length])) return true

      return false
    })

    const image = computed(() => {
      if (props.file) {
        // console.log(props.file)
        return props.file.url
      }
      return null
    })

    function openImageHandler(file: FileInfo) {
      Dialog.create({
        component: ImagePopup,
        url: file.url,
      })
    }

    return {
      isImage,
      hovered,
      image,
      downloadHandler,
      openImageHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.action-btn {
  transform-style: preserve-3d;
  background-color: black;
  color: white;
  position: absolute;
  z-index: 1;
  margin: 0;
  padding: 0;
  &:hover {
    transition: 0.35s ease-in-out;
    font-size: 10px !important;
    transform: translateX(4px) translateY(-3px);
  }
}

.img-block {
  &__image {
    transform-style: preserve-3d;

    &:hover {
      transition: 0.5s ease-in-out;
      transform: translateZ(15px) rotateZ(5deg);
    }
  }
}
</style>
