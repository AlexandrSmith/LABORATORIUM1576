<template>
  <div>
    <div v-if="!withoutHeader">
      <bordered-text-block done required without-question>
        <template #title>
          <div class="text-weight-bold" style="margin-left: 0">{{ project.name }}</div>
          <div>Предметная область: {{ project.subjectAreas.map((x) => x.title).join('; ') }}</div>
          <div>
            ФИО преподавателя:
            {{
              project.members.find((x) => x.type === MemberType.Teacher)
                ? project.members.find((x) => x.type === MemberType.Teacher).fullName
                : ''
            }}
          </div>
          <div>ФИО ученика: {{ project.learner.fullName }}</div>
          <div>Дата начала проекта: {{ isoToLocalDate(project.createDate) }}</div>
          <div v-if="project.stage === 4">Оценка: {{ project.mark }}</div>
          <div v-if="!full" class="green-link cursor-pointer q-mb-lg"></div>
        </template>
      </bordered-text-block>
    </div>
    <div v-if="full">
      <bordered-text-block
        v-if="project.researchTopic"
        label="Тема"
        :info="[project.researchTopic]"
        required
        bold
        tooltip="Тема"
      />
      <bordered-text-block
        v-if="project.researchObject"
        label="Объект"
        :info="[project.researchObject]"
        required
        bold
        tooltip="Объект"
      />
      <bordered-text-block
        v-if="project.researchSubject"
        label="Предмет"
        :info="[project.researchSubject]"
        required
        bold
        tooltip="Предмет"
      />
      <bordered-text-block
        v-if="project.researchPurpose"
        label="Цель"
        :info="[project.researchPurpose]"
        required
        bold
        tooltip="Цель"
      />

      <question-label label="Задачи" bold required tooltip="Задачи исследования" />
      <div v-for="(task, index) in project.tasks" :key="task.id" class="task">
        <div class="flex column justify-center task__task-title">{{ task.title }}</div>
        <div class="border q-my-sm" />
        <div class="task__task-result">{{ project.tasks[index].result || '' }}</div>
        <div class="border q-my-sm" />
        <div class="text-weight-bold">Вложения:</div>
        <div class="flex">
          <file-input
            v-for="file in task.files.filter((x) => x.pinned)"
            :key="file.fileId"
            class="flex"
            :file="file"
            read
          />
        </div>
      </div>

      <bordered-text-block
        v-if="project.researchHypothesis"
        label="Гипотеза"
        :info="[project.researchHypothesis]"
        required
        bold
        tooltip="Гипотеза"
      />
      <bordered-text-block
        v-if="project.researchMethods"
        label="Методы"
        :info="[project.researchMethods]"
        required
        bold
        tooltip="Методы"
      />
      <bordered-text-block
        v-if="project.workResult"
        label="Результат"
        :info="[project.workResult]"
        required
        bold
        tooltip="Результат"
      />
      <bordered-text-block
        v-if="project.conclusion"
        label="Выводы"
        :info="[project.conclusion]"
        required
        bold
        tooltip="Выводы"
      />
      <question-label label="Вложения" bold required tooltip="Задачи исследования" />
      <div class="flex">
        <file-input
          v-for="file in project.resultFiles.filter((x) => x.pinned)"
          :key="file.fileId"
          class="flex"
          :file="file"
          read
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref } from '@vue/composition-api'
import { ProjectDto } from 'src/models/Project'
import { isoToLocalDate } from 'src/utils/isoToLocalDate'
import { MemberType } from 'src/models/Project'
import BorderedTextBlock from 'components/base/BorderedTextBlock.vue'
import QuestionLabel from 'components/base/QuestionLabel.vue'
import FileInput from 'components/base/FileInput.vue'

export default defineComponent({
  name: 'ProjectTitle',
  components: {
    BorderedTextBlock,
    QuestionLabel,
    FileInput,
  },
  props: {
    full: Boolean,
    project: {
      type: Object as PropType<ProjectDto>,
      default: null,
    },
    withoutHeader: Boolean,
  },
  setup() {
    const tasks = ref(['задача 1', 'задача 2', 'задача 3'])
    return {
      tasks,
      isoToLocalDate,
      MemberType,
    }
  },
})
</script>

<style lang="scss" scoped>
.bordered-title {
  background: #fff7ea;
  border: 1px solid #d1b19c;
  box-sizing: border-box;
  border-radius: 10px;
}

.border {
  width: 100%;
  background-color: #bdbdbd;
  height: 1px;
}
</style>
