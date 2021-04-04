<template>
  <div>
    <div style="height: 90px; align-items: center; display: block">
      <progress-line
        class="flex justify-center"
        :active-step="activeStep"
        :max-step="clonedProject.stage"
        @progress-stepped="progressHandler"
      />
      <div class="flex justify-center">
        <div v-if="!isTeacher && !isExpert" style="width: 300px" class="flex justify-center q-my-sm">
          <styled-btn
            v-if="
              activeStep === clonedProject.stage &&
              clonedProject.stage < 4 &&
              clonedProject.members.length > 0 &&
              clonedProject.members.some((x) => x.status === MemberStatus.Accepted)
            "
            style="border-radius: 100px; margin-right: 20px"
            label="На проверку"
            color="secondary"
            outline
            :disable="clonedProject.waitingForStageUp"
            @click="waitingForStageUpHandler"
          />
          <styled-btn
            v-else-if="activeStep === 4"
            style="border-radius: 100px; margin-right: 20px"
            color="secondary"
            outline
            :disable="!clonedProject"
            :loading="generating"
            label="Сформировать отчет"
            @click="generateReport"
          />
          <div v-else />
          <styled-btn
            style="border-radius: 100px"
            :disable="!needsUpdate || !formIsValid"
            label="Сохранить"
            color="secondary"
            size="md"
            @click="saveClickHandler"
          >
            <q-tooltip v-if="!formIsValid">{{ saveBtnDisableText }}</q-tooltip>
          </styled-btn>
        </div>
        <div v-else class="flex justify-center q-my-sm">
          <div v-if="clonedProject.stage === 0">
            <styled-btn
              label="Отклонить приглашение"
              color="secondary"
              style="border-radius: 100px; margin-right: 20px"
              outline
              @click="acceptMentoringHandler(false)"
            />
            <styled-btn
              class="q-ml-sm"
              color="secondary"
              style="border-radius: 100px"
              label="Принять приглашение"
              @click="acceptMentoringHandler(true)"
            />
          </div>
          <styled-btn
            v-else-if="activeStep === 4"
            style="border-radius: 100px; margin-right: 20px"
            color="secondary"
            outline
            :disable="!clonedProject"
            :loading="generating"
            label="Сформировать отчет"
            @click="generateReport"
          />
          <span v-else />
          <div
            v-if="clonedProject.stage > 0 && activeStep === clonedProject.stage && isTeacher && clonedProject.stage < 4"
            class="flex"
          >
            <styled-btn
              v-if="clonedProject.waitingForStageUp"
              class="q-mr-md"
              color="secondary"
              outline
              style="border-radius: 100px; margin-right: 20px"
              :label="`Отклонить этап №${clonedProject.stage}`"
              @click="declineStageHandler"
            />
            <styled-btn
              style="border-radius: 100px"
              color="secondary"
              :label="`Принять этап №${clonedProject.stage}`"
              @click="acceptStageHandler"
            />
          </div>
        </div>
      </div>
    </div>

    <q-scroll-area style="height: calc(100vh - 150px)">
      <div
        v-if="deadline && isStudent && clonedProject.stage < 4"
        class="fullscreen"
        style="margin-left: 250px; margin-right: 400px"
      />
      <deadline-alert
        v-if="deadline && clonedProject.stage < 4"
        :is-student="isStudent"
        :title="
          clonedProject.locked ? 'Проект заблокирован!' : `Истек срок сдачи ${'I'.repeat(clonedProject.stage)} этапа!`
        "
      />
      <div v-if="isTeacher && clonedProject.stage < 4 && clonedProject.stage > 0">
        <div class="text-weight-bold">Срок сдачи текущего этапа</div>
        <div class="flex justify-start">
          <date-input v-model="clonedProject.deadline" />
          <styled-btn label="Продолжить" style="max-height: 40px; margin-left: 10px" @click="deadlineHandler" />
        </div>
      </div>
      <first-step
        v-if="activeStep === 1 || activeStep === 0"
        :validation-error="validationError"
        :is-teacher="isTeacher"
        :is-expert="isExpert"
        :is-student="isStudent"
        :project="clonedProject"
        @invite="inviteHandler"
        @update="
          (value) => {
            clonedProject = value
            needsUpdate = true
          }
        "
      />
      <second-step
        v-if="activeStep === 2"
        :is-teacher="isTeacher"
        :is-expert="isExpert"
        :is-student="isStudent"
        :project="clonedProject"
        @save="saveClickHandler"
        @update="
          (value) => {
            clonedProject = value
            needsUpdate = true
          }
        "
      />
      <third-step
        v-if="activeStep === 3"
        :is-teacher="isTeacher"
        :is-expert="isExpert"
        :is-student="isStudent"
        :validation-error="validationError"
        :project="clonedProject"
        @save="saveClickHandler"
        @update="
          (value) => {
            clonedProject = value
            needsUpdate = true
          }
        "
      />
      <fourth-step
        v-if="activeStep === 4"
        :is-teacher="isTeacher"
        :is-expert="isExpert"
        :is-student="isStudent"
        :project="clonedProject"
        @save-mark="saveMarkHandler"
        @save="saveClickHandler"
        @update="
          (value) => {
            clonedProject = value
            needsUpdate = true
          }
        "
      />

      <!--      <div class="full-width q-ma-lg" style="background-color: #e0e0e0; height: 1px" />-->
    </q-scroll-area>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from '@vue/composition-api'
import handleError, { handleApiError, handleGetError } from 'src/utils/errorHandler'
import { MemberStatus, MemberType, ProjectDto } from 'src/models/Project'
import * as R from 'ramda'
import FirstStep from 'components/project/FirstStep.vue'
import FourthStep from 'components/project/FourthStep.vue'
import SecondStep from 'components/project/SecondStep.vue'
import ThirdStep from 'components/project/ThirdStep.vue'
import ProgressLine from 'components/subcomponents/ProgressLine.vue'
import ProjectApi from 'src/api/ProjectApi'
import UserStore from 'src/store/modules/user'
import useRouter from 'src/router/useRouter'
import { isoToLocalDate, localDateToIso } from 'src/utils/isoToLocalDate'
import DeadlineAlert from './project/DeadlineAlert.vue'
import useRoute from 'src/router/useRoute'

export default defineComponent({
  name: 'Project',
  components: {
    FirstStep,
    SecondStep,
    ThirdStep,
    FourthStep,
    ProgressLine,
    DeadlineAlert,
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
    const router = useRouter()
    let route = useRoute()

    window.onbeforeunload = () => {
      return true
    }

    const activeStep = ref(+route.query.stage || props.project.stage)
    const clonedProject = ref(R.clone(props.project))
    const needsUpdate = ref(false)

    watch(
      () => router.currentRoute,
      () => {
        activeStep.value = +router.currentRoute.query.stage || props.project.stage
      }
    )

    watch(
      () => props.project,
      () => {
        if (clonedProject.value.id !== props.project.id)
          activeStep.value = +router.currentRoute.query.stage || props.project.stage
        clonedProject.value = R.clone(props.project)
        clonedProject.value.deadline = clonedProject.value.deadline ? isoToLocalDate(props.project.deadline || '') : ''
      }
    )

    const formIsValid = computed(() => {
      return (
        clonedProject.value.name?.length > 0 &&
        clonedProject.value.annotation?.length > 0 &&
        clonedProject.value.subjectAreas?.length > 0
        // clonedProject.value.researchTopic?.length > 0 &&
        // clonedProject.value.researchPurpose?.length > 0 &&
        // clonedProject.value.relevance?.length > 0
      )
    })

    const saveBtnDisableText = computed(() => {
      return clonedProject.value.name?.length === 0
        ? 'Поле "название проекта" не должно быть пустым'
        : clonedProject.value.annotation?.length === 0
        ? 'Поле "аннотация" не должно быть пустым'
        : clonedProject.value.subjectAreas?.length === 0
        ? 'Поле "предметная область" не должно быть пустым'
        : ''
      // : clonedProject.value.researchTopic?.length === 0 ? 'Поле "тема" не должно быть пустым'
      // : clonedProject.value.researchPurpose?.length === 0 ? 'Поле "цель" не должно быть пустым'
      // : clonedProject.value.relevance?.length === 0 ? 'Поле "актуальность" не должно быть пустым' : ''
    })

    const thirdStepFormIsValid = computed(() => {
      return clonedProject.value.workResult?.length > 0 && clonedProject.value.conclusion?.length > 0
    })

    const validationError = ref(false)

    function saveClickHandler() {
      if (activeStep.value < 2) {
        if (!formIsValid.value) {
          validationError.value = !validationError.value
          return
        }
      } else if (activeStep.value === 3) {
        if (!thirdStepFormIsValid.value) {
          validationError.value = !validationError.value
          return
        }
      }

      clonedProject.value.deadline = undefined
      needsUpdate.value = false
      if (clonedProject.value.id)
        ProjectApi.update(clonedProject.value.id, clonedProject.value)
          .then(() => context.emit('need-fetch'))
          .catch(handleApiError)
      else
        ProjectApi.create(clonedProject.value)
          .then((id) => {
            clonedProject.value.id = id
            context.emit('need-fetch')
            router.push({ name: 'Projects', params: { projectId: id.toString() } }).catch((e) => handleError(e))
          })
          .catch(handleApiError)
    }

    function saveMarkHandler() {
      if (clonedProject.value.id)
        ProjectApi.updateMark(clonedProject.value.id, clonedProject.value.mark)
          .then(() => context.emit('need-fetch'))
          .catch(handleApiError)
    }

    function progressHandler(step: number) {
      activeStep.value = step
      router
        .push({
          name: 'Projects',
          params: { projectId: props.project.id?.toString() || '' },
          query: { stage: step.toString() },
        })
        .catch((e) => console.log(e))
    }

    function acceptMentoringHandler(statement: boolean) {
      if (clonedProject.value.id)
        ProjectApi.acceptMentoring(clonedProject.value.id, statement)
          .then(() => {
            context.emit('need-fetch')
            if (statement && props.isTeacher) acceptStageHandler()
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            else if (!props.isExpert) context.root.$router.push({ name: 'PersonalArea' })
          })
          .catch(handleApiError)
    }

    function acceptStageHandler() {
      if (clonedProject.value.id)
        ProjectApi.acceptStage(clonedProject.value.id, clonedProject.value.stage + 1)
          .then(() => {
            activeStep.value++
            router
              .push({
                name: 'Projects',
                params: { projectId: props.project.id?.toString() || '' },
                query: { stage: activeStep.value.toString() },
              })
              .catch((e) => console.log(e))
            context.emit('need-fetch')
          })
          .catch(handleApiError)
    }

    function declineStageHandler() {
      if (clonedProject.value.id)
        ProjectApi.declineStage(clonedProject.value.id)
          .then(() => {
            context.emit('need-fetch')
          })
          .catch(handleApiError)
    }

    function waitingForStageUpHandler() {
      if (clonedProject.value.id)
        ProjectApi.waitingForStageUp(clonedProject.value.id, clonedProject.value.stage)
          .then(() => {
            context.emit('need-fetch')
          })
          .catch(handleApiError)
    }

    function inviteHandler(userIds: number[], type: MemberType) {
      if (clonedProject.value.id && userIds)
        ProjectApi.invite(clonedProject.value.id, userIds, type)
          .then(() => context.emit('need-fetch'))
          .catch(handleGetError)
    }

    function deadlineHandler() {
      context.emit('deadline', localDateToIso(clonedProject.value.deadline))
    }

    const deadline = computed(() => {
      return new Date() > new Date(props.project.deadline || '') || clonedProject.value.locked
    })

    const generating = ref(false)

    return {
      activeStep,
      generating,
      clonedProject,
      UserStore,
      needsUpdate,
      MemberStatus,
      deadline,
      validationError,
      saveBtnDisableText,

      formIsValid,
      thirdStepFormIsValid,
      progressHandler,
      isoToLocalDate,
      saveClickHandler,
      acceptMentoringHandler,
      acceptStageHandler,
      declineStageHandler,
      saveMarkHandler,
      deadlineHandler,
      waitingForStageUpHandler,
      inviteHandler,
      generateReport() {
        if (props.project.id) {
          generating.value = true
          ProjectApi.generateReport(props.project.id)
            .catch(handleApiError)
            .finally(() => (generating.value = false))
        }
      },
    }
  },
})
</script>

<style lang="scss" scoped></style>
