<template>
  <div>
    <teacher-project-title v-if="isTeacher || isExpert" :project="project" class="q-mb-md" />
    <project-title v-else :project="project" />

    <div class="text-weight-bold">Задачи исследования</div>
    <div
      v-for="(task, index) in clonedProject.tasks"
      :key="task.id"
      :class="{ 'task': clonedProject.tasks[index].isDone }"
    >
      <div class="flex justify-start">
        <q-checkbox v-if="!isTeacher && !isExpert" v-model="clonedProject.tasks[index].isDone" @input="updateHandler">
          <div class="flex column justify-center task__task-title">{{ task.title }}</div>
        </q-checkbox>
        <div v-else class="flex column justify-center task__task-title">{{ task.title }}</div>
      </div>
      <div v-if="clonedProject.tasks[index].isDone">
        <div v-if="isTeacher || isExpert" class="task__result-title">
          <div class="border q-my-sm" />
          <div class="task__task-result">{{ project.tasks[index].result || '' }}</div>
          <div class="border q-my-sm" />
          <div class="text-weight-bold">Вложения:</div>
        </div>
        <styled-input
          v-else
          v-model="clonedProject.tasks[index].result"
          hide-bottom-space
          type="textarea"
          :rows="4"
          placeholder="Результат работы над задачей"
          @input="updateHandler"
        />
        <div class="flex">
          <!--        <q-btn-->
          <!--          v-if="!isTeacher && !isExpert"-->
          <!--          class="q-mr-sm"-->
          <!--          icon="app:minus-circle"-->
          <!--          flat-->
          <!--          round-->
          <!--          @click="deleteFileHandler(task.id, file)"-->
          <!--        />-->
          <!--        <div class="flex column justify-center q-mr-sm">{{ file.filename }}</div>-->
          <!--        <q-btn icon="app:download" flat round @click="downloadHandler(file)" />-->
          <file-input
            v-for="file in task.files"
            :key="file.fileId"
            :file="file"
            :read="!isStudent"
            @delete="(file) => deleteFileHandler(task.id, file)"
            @pin="pinFileHandler"
          />
        </div>
        <styled-btn v-if="!isTeacher && !isExpert" class="q-mt-sm" outline @click="fileInputHandler(task.id)"
          >Прикрепить файл</styled-btn
        >
      </div>
    </div>
    <input ref="fileInput" type="file" style="display: none" @input="addFileHandler" />
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, watch } from '@vue/composition-api'
import ProjectTitle from 'components/project/ProjectTitle.vue'
import TeacherProjectTitle from 'components/project/TeacherProjectTitle.vue'
import FileInput from 'components/base/FileInput.vue'
import { FileDto, FileInfo, ProjectDto } from 'src/models/Project'
import * as R from 'ramda'
import ProjectApi from 'src/api/ProjectApi'
import { handleApiError } from 'src/utils/errorHandler'
import { downloadHandler } from 'src/utils/downloadLink'

export default defineComponent({
  name: 'SecondStep',
  components: {
    ProjectTitle,
    TeacherProjectTitle,
    FileInput,
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
    const currentTaskNumber = ref(0)
    const fileInput = ref<HTMLInputElement | null>(null)

    watch(
      () => props.project,
      () => {
        clonedProject.value = R.clone(props.project)
      }
    )

    function updateHandler() {
      context.emit('update', clonedProject.value)
    }

    function fileInputHandler(taskNumber: number) {
      currentTaskNumber.value = taskNumber
      fileInput.value?.click()
    }

    function deleteFileHandler(taskId: number, file: FileInfo) {
      if ('fileId' in file) {
        ProjectApi.deleteFile(taskId, file.fileId || 0)
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

    function addFileHandler(event: InputEvent) {
      const target = event.target as HTMLInputElement
      if (target && target.files && target.files[0]) {
        const file = target.files[0]
        context.emit('save', clonedProject.value)
        const newFile: Partial<FileDto> = {
          projectId: props.project.id || 0,
          taskId: currentTaskNumber.value,
          file: typeof file === 'string' ? undefined : file,
        }
        ProjectApi.addFile(props.project.id || 0, newFile)
          .then(() => context.emit('save', clonedProject.value))
          .catch(handleApiError)
      }
      updateHandler()
    }

    return {
      clonedProject,
      fileInput,
      currentTaskNumber,

      updateHandler,
      addFileHandler,
      downloadHandler,
      fileInputHandler,
      deleteFileHandler,
      pinFileHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.border {
  width: 100%;
  background-color: #bdbdbd;
  height: 1px;
}
</style>
