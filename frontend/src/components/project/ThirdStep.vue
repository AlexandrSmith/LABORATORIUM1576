<template>
  <div>
    <teacher-project-title v-if="isTeacher || isExpert" :project="project" class="q-mb-md" />
    <project-title v-else :project="project" />

    <info-block
      ref="workResult"
      label="Результат работы"
      tooltip="Результат работы"
      :info="clonedProject.workResult"
      required
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.workResult = value
          updateHandler()
        }
      "
    />

    <info-block
      ref="conclusion"
      label="Выводы"
      tooltip="Выводы"
      :info="clonedProject.conclusion"
      required
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.conclusion = value
          updateHandler()
        }
      "
    />

    <div class="text-weight-bold q-mt-sm">Вложения</div>

    <div class="flex">
      <file-input
        v-for="file in clonedProject.resultFiles"
        :key="file.fileId"
        :file="file"
        @delete="deleteFileHandler"
        @pin="pinFileHandler"
      />
      <!-- <file-input v-for="file in clonedProject.resultFiles" :key="file.fileId" :file="file" /> -->
    </div>
    <styled-btn v-if="!isTeacher && !isExpert" class="q-mt-sm" outline @click="fileInputHandler"
      >Прикрепить файл</styled-btn
    >
    <input ref="fileInput" type="file" style="display: none" @input="addFileHandler" />
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, watch } from '@vue/composition-api'
import ProjectTitle from 'components/project/ProjectTitle.vue'
import TeacherProjectTitle from 'components/project/TeacherProjectTitle.vue'
import { FileDto, FileInfo, InfoBlockRef, ProjectDto } from 'src/models/Project'
import * as R from 'ramda'
import ProjectApi from 'src/api/ProjectApi'
import { handleApiError } from 'src/utils/errorHandler'
import { downloadHandler } from 'src/utils/downloadLink'
import FileInput from 'components/base/FileInput.vue'
import InfoBlock from 'components/base/InfoBlock.vue'

export default defineComponent({
  name: 'ThirdStep',
  components: {
    ProjectTitle,
    TeacherProjectTitle,
    FileInput,
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
    validationError: Boolean,
  },
  setup(props, context) {
    const clonedProject = ref(R.clone(props.project))
    const fileInput = ref<HTMLInputElement | null>(null)

    watch(
      () => props.project,
      () => {
        clonedProject.value = R.clone(props.project)
      }
    )
    const activeMark = ref(5)

    const workResult = ref<InfoBlockRef | null>(null)
    const conclusion = ref<InfoBlockRef | null>(null)

    watch(
      () => props.validationError,
      () => {
        if (clonedProject.value.workResult.length === 0 && workResult.value) workResult.value.focusInput.focus()
        else if (clonedProject.value.conclusion.length === 0 && conclusion.value) conclusion.value.focusInput.focus()
      }
    )

    function updateHandler() {
      context.emit('update', clonedProject.value)
    }

    function fileInputHandler() {
      fileInput.value?.click()
    }

    function addFileHandler(event: InputEvent) {
      const target = event.target as HTMLInputElement
      if (target && target.files && target.files[0]) {
        const file = target.files[0]
        const newFile: Partial<FileDto> = {
          projectId: props.project.id || 0,
          file: typeof file === 'string' ? undefined : file,
        }
        ProjectApi.addFile(props.project.id || 0, newFile)
          .then(() => context.emit('save', clonedProject.value))
          .catch(handleApiError)
      }
      updateHandler()
    }

    function deleteFileHandler(file: FileInfo) {
      console.log(file)
      if ('fileId' in file) {
        ProjectApi.deleteFile(props.project.id || 0, file.fileId || 0)
          .then(() => context.emit('save', clonedProject.value))
          .catch(handleApiError)
      }
      updateHandler()
    }

    function pinFileHandler(file: FileInfo) {
      if (props.project.id && file.fileId) {
        ProjectApi.pinFile(props.project.id, file.fileId, !file.pinned)
          .then(() => {
            context.emit('save', clonedProject.value)
          })
          .catch(handleApiError)
      }
      updateHandler()
    }

    return {
      activeMark,
      clonedProject,
      fileInput,
      workResult,
      conclusion,

      pinFileHandler,
      addFileHandler,
      updateHandler,
      deleteFileHandler,
      fileInputHandler,
      downloadHandler,
    }
  },
})
</script>

<style lang="scss" scoped></style>
