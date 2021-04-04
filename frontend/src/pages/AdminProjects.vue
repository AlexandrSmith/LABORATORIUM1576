<template>
  <div class="page flex justify-center column">
    <h5 class="text-weight-bold">Проекты</h5>
    <styled-input v-model="search" label="Поиск" debounce="500" @input="fetch" />
    <q-item v-for="project in projects" :key="project.id" class="project-block q-mb-md">
      <div class="column full-width">
        <div class="flex justify-between q-mb-sm">
          <div class="column">
            <div class="project-block__title">{{ project.name }}</div>
            <div class="project-block__subject-areas">{{ project.subjectAreas.map((x) => x.title).join('; ') }}</div>
          </div>
          <div class="flex">
            <styled-btn
              icon="app:edit"
              flat
              round
              color="black"
              :to="{ name: 'Projects', params: { projectId: project.id } }"
            />
            <styled-btn icon="app:minus-circle" flat round color="black" @click="deleteProjectHandler" />
          </div>
        </div>
        <div class="project-block__text q-mb-sm">{{ project.annotation }}</div>
        <div
          class="project-block__link"
          @click="
            showDetailedInfo.includes(project.id)
              ? (showDetailedInfo = showDetailedInfo.filter((x) => x !== project.id))
              : showDetailedInfo.push(project.id)
          "
        >
          Подробная информация о проекте
        </div>
        <project-title v-if="showDetailedInfo.includes(project.id)" without-header full :project="project" />
        <div class="flex justify-between q-mt-md">
          <div class="flex">
            <div class="text-weight-bold q-mr-sm">Преподаватель:</div>
            <div class="q-mr-lg">
              {{
                project.members.find((x) => x.type === MemberType.Teacher)
                  ? project.members.find((x) => x.type === MemberType.Teacher).fullName
                  : '-'
              }}
            </div>
            <div class="text-weight-bold q-mr-sm">Ученик:</div>
            <div>{{ project.learner ? project.learner.fullName : '-' }}</div>
          </div>
          <div class="flex">
            <div class="text-weight-bold q-mr-sm" q-mr-sm>Оценка:</div>
            <div>{{ project.mark }}</div>
          </div>
        </div>
      </div>
    </q-item>
    <q-inner-loading :showing="loading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from '@vue/composition-api'
import handleError, { handleApiError, handleGetError } from 'src/utils/errorHandler'
import { Dialog } from 'quasar'
import { MemberType, ProjectDto } from 'src/models/Project'
import ProjectApi from 'src/api/ProjectApi'
import useRouter from 'src/router/useRouter'
import ProjectTitle from 'components/project/ProjectTitle.vue'

export default defineComponent({
  name: 'AdminProjects',
  components: { ProjectTitle },
  setup() {
    const loading = ref(false)
    const projects = ref<ProjectDto[]>([])

    const search = ref('')
    const showDetailedInfo = ref<number[]>([])

    const params = computed(() => {
      const params = new URLSearchParams()
      if (search.value.length > 0) params.append('name', search.value)
      return params
    })

    function fetch() {
      ProjectApi.adminFilteredList(params.value)
        .then((payload) => {
          projects.value = payload
        })
        .catch(handleGetError)
    }
    fetch()

    function deleteProjectHandler(id: number) {
      Dialog.create({
        message: 'Вы уверены?',
        cancel: true,
      }).onOk(() => {
        ProjectApi.delete(id).then(fetch).catch(handleApiError)
      })
    }

    function openProjectHandler(id: number) {
      const router = useRouter()
      router.push({ name: 'Projects', params: { projectId: id.toString() } }).catch(handleError)
    }

    return {
      loading,
      projects,
      search,
      showDetailedInfo,
      MemberType,

      fetch,
      deleteProjectHandler,
      openProjectHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.project-block {
  border: 1px solid #bdbdbd;
  box-sizing: border-box;
  border-radius: 10px;

  &__title {
    font-weight: bold;
    font-size: 16px;
    color: #000000;
  }

  &__author {
    font-weight: bold;
    font-size: 14px;
  }

  &__annotation {
    font-size: 16px;
    font-style: italic;
  }

  &__subject-areas {
    font-size: 12px;
    color: #9f9f9f;
  }

  &__text {
    font-weight: 300;
    font-size: 14px;
    color: #000000;
  }

  &__link {
    font-weight: 300;
    font-size: 14px;
    color: #ff6600;
    cursor: pointer;
  }
}
</style>
