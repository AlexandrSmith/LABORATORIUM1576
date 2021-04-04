<template>
  <div>
    <q-list style="height: calc(100vh - 120px)">
      <q-item v-if="!UserStore.isAdmin" class="item-block flex" @click.native="showProjects = !showProjects">
        <styled-btn
          :icon="showProjects ? 'app:projects-open' : 'app:projects-close'"
          round
          flat
          color="secondary"
          style="margin-top: -4px"
        />
        <div class="menu-text flex justify-center column">{{ items[0] }}</div>
      </q-item>
      <q-scroll-area v-if="showProjects && !UserStore.isAdmin" style="height: calc(100% - 50px)">
        <q-item
          v-for="(project, index) in MenuStore.projectList"
          :key="index"
          class="project-block flex column cursor-pointer"
          style="padding-left: 50px"
          active-class="active"
          :to="{ name: 'Projects', params: { projectId: project.id }, query: { stage: project.stage } }"
        >
          <div class="flex justify-between">
            <div>{{ project.name }}</div>
            <div class="text-red-10">
              {{
                NotificationStore.getNotifications.find((x) => x.project === project.id)
                  ? NotificationStore.getNotifications.find((x) => x.project === project.id).quantity
                  : ''
              }}
            </div>
          </div>
          <div
            :class="{
              'in-work-text': project.stage >= 0 && project.stage < 4,
              'done-text': project.stage === 4,
            }"
          >
            {{ projectStage(project) }}
          </div>
          <div class="date-text">{{ project.createDate }}</div>
        </q-item>
      </q-scroll-area>
      <!--    <q-item-->
      <!--      v-if="UserStore.isStudent"-->
      <!--      class="item-block flex"-->
      <!--      active-class="active"-->
      <!--      :to="{ name: 'Projects', params: { projectId: 'new' } }"-->
      <!--    >-->
      <!--      <q-btn icon="app:project" size="md" color="orange" round style="margin-top: -4px" />-->
      <!--      <div class="menu-text flex justify-center column">{{ items[4] }}</div>-->
      <!--    </q-item>-->
      <q-item v-if="UserStore.isAdmin" class="item-block flex" active-class="active" :to="{ name: 'Users' }">
        <styled-btn icon="app:users" round flat color="secondary" style="margin-top: -4px" />
        <div class="menu-text flex justify-center column">{{ items[2] }}</div>
      </q-item>
      <q-item v-if="UserStore.isAdmin" class="item-block flex" active-class="active" :to="{ name: 'Subjects' }">
        <styled-btn icon="app:subjects" round flat color="secondary" style="margin-top: -4px" />
        <div class="menu-text flex justify-center column">{{ items[3] }}</div>
      </q-item>
      <q-item v-if="UserStore.isAdmin" class="item-block flex" active-class="active" :to="{ name: 'News' }">
        <styled-btn icon="app:subjects" round flat color="secondary" style="margin-top: -4px" />
        <div class="menu-text flex justify-center column">{{ 'Новости' }}</div>
      </q-item>
      <q-item v-if="UserStore.isAdmin" class="item-block flex" active-class="active" :to="{ name: 'AdminProjects' }">
        <styled-btn icon="app:subjects" round flat color="secondary" style="margin-top: -4px" />
        <div class="menu-text flex justify-center column">{{ 'Проекты' }}</div>
      </q-item>
    </q-list>
    <div
      v-if="UserStore.isStudent"
      class="flex cursor-pointer img-block"
      style="position: absolute; bottom: 20px; left: 20px"
      @click="$router.push({ name: 'Projects', params: { projectId: 'new' } })"
    >
      <!-- <styled-btn icon="app:project" color="secondary" round style="margin-top: -4px" /> -->
      <div class="flex justify-center img-block__circle">
        <div class="flex justify-center column">
          <q-img class="img-block__image" src="new-project.svg" width="30px" height="34px"></q-img>
        </div>
      </div>
      <div class="menu-text flex justify-center column q-ml-sm text-weight-bold">Новый проект</div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from '@vue/composition-api'
import { ProjectDto, ProjectInfoDto, ProjectStatus } from 'src/models/Project'
import { translate } from 'src/utils/translate'
import { handleGetError } from 'src/utils/errorHandler'
import ProjectApi from 'src/api/ProjectApi'
import UserStore from 'src/store/modules/user'
import NotificationStore from 'src/store/modules/notification'
import MenuStore from 'src/store/modules/menu'

export default defineComponent({
  name: 'SideMenu',
  props: {
    needFetch: Boolean,
  },
  setup(props, context) {
    // const items = ref<string[]>(['Проекты в работе', 'Темы проектов'])
    const showProjects = ref(true)
    const items = ref<string[]>(['Мои проекты', 'Темы проектов', 'Пользователи', 'Предметные области', 'Новый проект'])
    const projects = ref<ProjectInfoDto[]>([])
    projects.value = []

    function fetch() {
      console.log('fetch')
      ProjectApi.list()
        .then((payload) => {
          projects.value = payload
        })
        .catch(handleGetError)
    }
    fetch()

    watch(
      () => props.needFetch,
      () => {
        console.log('fetch')
        fetch()
      }
    )

    function checkRouter() {
      console.log(context.root.$router.currentRoute)
      console.log(context.root.$router.push({ path: '404' }))
    }

    MenuStore.fetchProjects().catch(handleGetError)

    return {
      projects,
      items,
      showProjects,
      UserStore,
      NotificationStore,
      MenuStore,
      ProjectStatus,

      projectStage(project: ProjectDto) {
        return project.stage > 0
          ? project.stage === 4
            ? `завершен (оценка ${project.mark || 'нет'})`
            : `в работе (${'I'.repeat(project.stage)} этап)`
          : 'в работе (I этап)'
      },

      checkRouter,
      translate,
    }
  },
})
</script>

<style lang="scss" scoped>
.menu-text {
  font-size: 16px;
}

.active {
  background-color: white;
}

.item-block {
  height: 40px;
  cursor: pointer;
}

.waiting-text {
  color: #eb5757;
  font-weight: 300;
}

.done-text {
  color: #828282;
  font-weight: 300;
}

.in-work-text {
  color: #219653;
  font-weight: 300;
}

.date-text {
  font-weight: 300;
  color: #828282;
}

.img-block {
  perspective: 100px;

  &__circle {
    background: #ff6600;
    border-radius: 50%;
    position: relative;
    width: 64px;
    height: 64px;

    filter: drop-shadow(0px 4px 0px rgba(255, 168, 0, 0.24));
    &:active {
      transition: 0.2s ease-in-out;
      transform: translateZ(-10px);
      filter: none;
    }
  }

  &__image {
    &:hover {
      transition: 0.5s ease-in-out;
      transform: translateZ(20px) rotateZ(-20deg);
    }
  }
}
</style>
