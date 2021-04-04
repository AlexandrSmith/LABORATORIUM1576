<template>
  <label class="styled-image" :style="!deleteBtn ? 'cursor: pointer' : null">
    <span>{{ label }}</span>
    <q-img
      contain
      class="image"
      :src="src"
      :height="height"
      width="width"
      placeholder-src="placeholder.png"
      @click="onClick"
    >
      <q-icon v-if="deleteBtn" name="app:close-circle" class="delete-btn" @click.stop="$emit('delete')" />
    </q-img>
    <input v-if="selectable" ref="fileRef" type="file" style="display: none" @change="onFileInput" />
  </label>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'

export default defineComponent({
  name: 'StyledImage',
  props: {
    src: { type: String, default: null },
    label: { type: String, default: null },
    deleteBtn: { type: Boolean, default: false },
    selectable: {
      type: Boolean,
      default: false,
    },
    height: {
      type: String,
      default: '50px',
    },
    width: {
      type: String,
      default: '50px',
    },
  },
  setup(props, { emit }) {
    const fileRef = ref<HTMLInputElement>()

    function onClick() {
      if (props.selectable && fileRef.value !== undefined) {
        fileRef.value?.click()
      } else {
        emit('click')
      }
    }

    function onFileInput(event: InputEvent) {
      const target = event.target as HTMLInputElement
      if (target.files && target.files.length) {
        const file = target.files[0]
        const image = URL.createObjectURL(file)
        emit('update:src', image)
      }
    }

    return {
      onClick,
      onFileInput,
    }
  },
})
</script>

<style lang="scss">
.styled-image {
  /*--image-width: 100px;*/
  /*--image-height: 100px;*/

  > .image {
    position: relative;
    border: 1px solid #e0e0e0;
    border-radius: 4px;

    /*  width: var(--image-width);*/
    /*  height: var(--image-height);*/
  }

  .delete-btn {
    position: absolute;
    top: 3px;
    right: 3px;
    max-width: 12px;
    max-height: 12px;
    cursor: pointer;
  }
}
</style>
