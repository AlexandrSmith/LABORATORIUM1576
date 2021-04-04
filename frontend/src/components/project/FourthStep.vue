<template>
  <div>
    <project-title full :project="project" />

    <info-block
      label="Пояснительная записка"
      tooltip="Пояснительная записка"
      :info="clonedProject.explanatory"
      question-label
      required
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.explanatory = value
          updateHandler()
        }
      "
    />

    <div v-if="isTeacher">
      <h5 class="text-weight-bold" style="margin-left: 0">Оценка</h5>
      <div class="flex">
        <div
          v-for="i in 5"
          :key="i"
          class="mark-block"
          :class="{ 'mark-block--active': clonedProject.mark === i }"
          @click="
            () => {
              clonedProject.mark = i
              updateHandler()
              saveMarkHandler()
            }
          "
        >
          {{ i }}
        </div>
      </div>
    </div>

    <div class="flex justify-end q-mb-md"></div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, watch } from '@vue/composition-api'
import { ProjectDto } from 'src/models/Project'
import { handleApiError } from 'src/utils/errorHandler'
import InfoBlock from 'components/base/InfoBlock.vue'
import ProjectTitle from 'components/project/ProjectTitle.vue'
import ProjectApi from 'src/api/ProjectApi'
import * as R from 'ramda'

export default defineComponent({
  name: 'FourthStep',
  components: {
    ProjectTitle,
    InfoBlock,
  },
  props: {
    project: {
      type: Object as PropType<ProjectDto>,
      default: null,
    },
    isTeacher: Boolean,
    isExpert: Boolean,
    isStudent: Boolean,
  },
  setup(props, context) {
    const clonedProject = ref(R.clone(props.project))

    watch(
      () => props.project,
      () => {
        clonedProject.value = R.clone(props.project)
      }
    )

    function saveMarkHandler() {
      context.emit('save-mark', clonedProject.value)
    }

    function updateHandler() {
      context.emit('update', clonedProject.value)
    }

    return {
      clonedProject,

      saveMarkHandler,
      updateHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.mark-block {
  width: 50px;
  height: 28px;
  color: #2f80ed;
  border: 1px solid #2f80ed;
  border-radius: 4px;
  text-align: center;
  margin-right: 5px;
  cursor: pointer;
  font-size: 16px;

  &--active {
    background: #2f80ed;
    color: #fff;
  }
}
</style>
