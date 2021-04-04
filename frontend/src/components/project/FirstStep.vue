<template>
  <div>
    <div v-if="isTeacher || isExpert">
      <teacher-project-title :project="project" class="q-mb-md" />
    </div>
    <div v-else>
      <h5 class="text-weight-bold" style="margin-left: 0">{{ project.id ? `${project.name}` : 'Без названия' }}</h5>
      <styled-input ref="name" v-model="clonedProject.name" label="Название проекта" required @input="updateHandler" />
      <info-block
        ref="annotation"
        label="Аннотация"
        tooltip="Аннотация"
        :info="clonedProject.annotation"
        required
        question-label
        :is-student="isStudent"
        @input="
          (value) => {
            clonedProject.annotation = value
            updateHandler()
          }
        "
      />
      <styled-select
        :value="clonedProject.subjectAreas.map((x) => x.id)"
        :options="
          subjects.map((x) => {
            return { value: x.id, label: x.title }
          })
        "
        emit-value
        map-options
        multiple
        required
        label="Предметная область"
        @input="subjectAreaHandler"
      />
      <div v-if="!clonedProject.members.find((x) => x.type === MemberType.Teacher)" class="flex justify-between">
        <q-tooltip v-if="!project.id">Необходимо сохранить проект</q-tooltip>
        <styled-select
          v-model="teacherId"
          :options="teachersList"
          emit-value
          map-options
          style="width: 300px"
          label="Руководитель проекта"
          :disable="!clonedProject.id"
          @input="teacherSelectionDisabled = false"
        />
        <div class="q-ml-md">
          <styled-btn
            style="height: 40px; margin-top: 25px"
            label="Пригласить"
            :disable="!teacherId || teacherSelectionDisabled || !project.id"
            @click="
              () => {
                $emit('invite', [teacherId], MemberType.Teacher)
                teacherSelectionDisabled = true
              }
            "
          >
          </styled-btn>
        </div>
      </div>
      <div v-else>
        <styled-select
          :value="clonedProject.members.find((x) => x.type === MemberType.Teacher).fullName || ''"
          :options="[]"
          readonly
          style="width: 300px"
          label="Руководитель проекта"
        />
      </div>
    </div>

    <info-block
      ref="topic"
      label="Тема"
      tooltip="Тема проекта"
      :info="clonedProject.researchTopic"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.researchTopic = value
          updateHandler()
        }
      "
    />
    <info-block
      ref="purpose"
      label="Цель"
      tooltip="Цель проекта"
      :info="clonedProject.researchPurpose"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.researchPurpose = value
          updateHandler()
        }
      "
    />

    <div class="q-mb-md">
      <question-label label="Задачи" bold required tooltip="Задачи исследования" />
      <div v-for="(task, index) in clonedProject.tasks" :key="task.id">
        <div class="flex justify-start">
          <div v-if="isTeacher || isExpert" style="width: 300px; height: 40px" class="flex column justify-center">
            {{ task.title }}
          </div>
          <div v-else class="flex">
            <styled-input
              v-model="clonedProject.tasks[index].title"
              style="width: 300px"
              placeholder="Задача"
              @input="updateHandler"
            />
            <q-btn
              style="height: 40px"
              icon="app:minus-circle"
              flat
              round
              @click="
                () => {
                  clonedProject.tasks.splice(index, 1)
                  updateHandler()
                }
              "
            />
          </div>
        </div>
      </div>
      <styled-btn
        v-if="!isTeacher && !isExpert"
        outline
        label="Добавить задачу"
        :disable="!project.id"
        @click="addTaskHandler"
      >
        <q-tooltip v-if="!project.id">Необходимо сохранить проект</q-tooltip>
      </styled-btn>
    </div>

    <info-block
      ref="relevance"
      label="Актуальность"
      tooltip="Актуальность проекта"
      :info="clonedProject.relevance"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.relevance = value
          updateHandler()
        }
      "
    />

    <info-block
      label="Объект"
      tooltip="Объект исследования"
      :info="clonedProject.researchObject"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.researchObject = value
          updateHandler()
        }
      "
    />
    <info-block
      label="Предмет"
      tooltip="Предмет исследования"
      :info="clonedProject.researchSubject"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.researchSubject = value
          updateHandler()
        }
      "
    />
    <info-block
      label="Гипотеза"
      tooltip="Гипотеза исследования"
      :info="clonedProject.researchHypothesis"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.researchHypothesis = value
          updateHandler()
        }
      "
    />
    <info-block
      label="Методы"
      tooltip="Методы исследования"
      :info="clonedProject.researchMethods"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.researchMethods = value
          updateHandler()
        }
      "
    />

    <info-block
      label="Проблема"
      tooltip="Проблема"
      :info="clonedProject.problems"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.problems = value
          updateHandler()
        }
      "
    />
    <info-block
      label="Продукт"
      tooltip="Продукт"
      :info="clonedProject.product"
      question-label
      :is-student="isStudent"
      @input="
        (value) => {
          clonedProject.product = value
          updateHandler()
        }
      "
    />

    <div v-if="isTeacher && clonedProject.stage > 0" class="flex justify-start">
      <q-select
        v-model="expertsId"
        :options="expertsList"
        multiple
        emit-value
        map-options
        style="width: 300px"
        label="Назначить эксперта"
        @input="expertSelectionDisabled = false"
      />
      <div class="flex column justify-center q-ml-sm">
        <styled-btn
          :disable="expertSelectionDisabled"
          label="Пригласить"
          outline
          style="margin-right: 10px"
          @click="
            () => {
              $emit('invite', expertsId, MemberType.Expert)
              expertSelectionDisabled = true
            }
          "
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, watch } from '@vue/composition-api'
import { InfoBlockRef, MemberType, OptionValue, ProjectDto, SubjectAreaDto } from 'src/models/Project'
import { handleGetError } from 'src/utils/errorHandler'
import { fullName } from 'src/utils/fullName'
import TeacherProjectTitle from 'components/project/TeacherProjectTitle.vue'
import * as R from 'ramda'
import ProjectApi from 'src/api/ProjectApi'
import InfoBlock from 'components/base/InfoBlock.vue'
import QuestionLabel from 'components/base/QuestionLabel.vue'
import StyledBtn from 'components/base/StyledBtn.vue'
import { QInput } from 'quasar'

export default defineComponent({
  name: 'FirstStep',
  components: {
    TeacherProjectTitle,
    InfoBlock,
    QuestionLabel,
    StyledBtn,
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
    const subjects = ref<SubjectAreaDto[]>([])
    const loading = ref(false)
    const teacherId = ref<number | null>(null)
    const expertsId = ref<number[]>([])
    const teacherSelectionDisabled = ref(true)
    const expertSelectionDisabled = ref(true)

    const name = ref<InfoBlockRef | null>(null)
    const annotation = ref<InfoBlockRef | null>(null)
    const purpose = ref<InfoBlockRef | null>(null)
    const topic = ref<InfoBlockRef | null>(null)
    const relevance = ref<InfoBlockRef | null>(null)

    watch(
      () => props.validationError,
      () => {
        if (clonedProject.value.name.length === 0 && name.value) name.value.focusInput.focus()
        else if (
          clonedProject.value.annotation.length === 0 &&
          annotation.value &&
          'focusInput' in annotation.value &&
          'focus' in annotation.value.focusInput
        )
          annotation.value.focusInput.focus()
        else if (clonedProject.value.researchPurpose.length === 0 && purpose.value) purpose.value.focusInput.focus()
        else if (clonedProject.value.researchTopic.length === 0 && topic.value) topic.value.focusInput.focus()
        else if (clonedProject.value.relevance.length === 0 && relevance.value) relevance.value.focusInput.focus()
      }
    )

    function fillUserIds() {
      if (clonedProject.value.members)
        teacherId.value = clonedProject.value.members.find((x) => x.type === MemberType.Teacher)?.userId || null
      if (clonedProject.value.members)
        expertsId.value =
          clonedProject.value.members.filter((x) => x.type === MemberType.Expert).map((x) => x.userId) || null
    }
    fillUserIds()

    function fetchSubjects() {
      loading.value = true
      ProjectApi.subjects
        .list()
        .then((payload) => {
          subjects.value = payload
          loading.value = false
        })
        .catch(handleGetError)
    }
    fetchSubjects()

    watch(
      () => props.project,
      () => {
        clonedProject.value = R.clone(props.project)
        fillUserIds()
      }
    )

    function updateHandler() {
      context.emit('update', clonedProject.value)
    }

    const tasks = ref(['задача 1', 'задача 2', 'задача 3'])
    const teachersList = ref<OptionValue[]>([])
    const expertsList = ref<OptionValue[]>([])

    function fetchTeachersAndExperts() {
      let idsList: number[] = []
      clonedProject.value.subjectAreas.forEach((x) => (x.id ? idsList.push(x.id) : null))
      ProjectApi.teachersList(idsList)
        .then(
          (payload) =>
            (teachersList.value = payload.map((x) => {
              return { value: x.id!, label: fullName(x) || '' }
            }))
        )
        .catch(handleGetError)
      ProjectApi.expertsList(idsList)
        .then(
          (payload) =>
            (expertsList.value = payload.map((x) => {
              return { value: x.id!, label: fullName(x) || '' }
            }))
        )
        .catch(handleGetError)
    }
    fetchTeachersAndExperts()

    function subjectAreaHandler(value: number[]) {
      clonedProject.value.subjectAreas = []
      value.forEach((x) => {
        const subject = subjects.value.find((y) => y.id === x)
        if (subject) clonedProject.value.subjectAreas.push(subject)
      })
      updateHandler()
    }

    function addTaskHandler() {
      clonedProject.value.tasks.push({
        title: '',
        projectId: props.project.id || 0,
        isDone: false,
        result: '',
        files: [],
      })
      updateHandler()
    }

    return {
      tasks,
      clonedProject,
      teachersList,
      expertsList,
      subjects,
      loading,
      teacherId,
      expertsId,
      teacherSelectionDisabled,
      expertSelectionDisabled,
      MemberType,
      topic,
      annotation,
      relevance,
      name,
      purpose,

      subjectAreaHandler,
      addTaskHandler,
      updateHandler,
    }
  },
})
</script>

<style lang="scss" scoped></style>
