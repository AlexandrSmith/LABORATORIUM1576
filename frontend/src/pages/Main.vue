<template>
  <q-scroll-area class="fullscreen main-page" style="min-height: 100vh; padding-bottom: 20px">
    <div class="flex justify-center">
      <div class="q-mb-lg column justify-center main-header">
        <div class="flex justify-start">
          <q-img width="74px" height="40px" src="logo.svg" />
        </div>
        <div class="flex column justify-end q-mr-md">
          <router-link
            :to="UserStore.loggedIn ? 'lk' : '/login'"
            class="flex cursor-pointer justify-end main-page__header--link"
          >
            <q-icon size="22px" class="q-mr-sm" name="app:account" />
            <div style="font-size: 16px">{{ UserStore.loggedIn ? 'Личный кабинет' : 'Войти' }}</div>
          </router-link>
        </div>
      </div>
    </div>

    <div class="flex justify-center q-mb-lg">
      <div class="main-page__inner-container full-height">
        <header class="main-page__header q-mb-lg">
          <!--          <q-img width="235px" height="135px" src="logo.svg" />-->
          <div class="flex column">
            <div class="flex justify-center main-page__header align q-mb-lg">Laboratorium 1576</div>
            <!--            <div class="flex justify-center main-page__header&#45;&#45;sub q-mb-sm" style="margin-bottom: 80px">-->
            <!--              {{ currentPath === 'Каталог проектов' ? 'Каталог проектов' : 'Лента новостей' }}-->
            <!--            </div>-->
            <div class="flex justify-center q-mb-lg">
              <styled-btn
                outline
                color="#9D9D9D"
                @click="
                  () => {
                    currentPath = currentPath === 'Главная' ? 'Каталог проектов' : 'Главная'
                    currentProject = null
                    currentNews = null
                  }
                "
              >
                {{ currentPath === 'Главная' ? 'Каталог проектов' : 'Главная' }}
              </styled-btn>
            </div>
          </div>
        </header>
      </div>
    </div>

    <div class="flex justify-center">
      <div class="main-page__inner-container full-height">
        <section v-if="currentPath || currentProject" class="main-page__nav-bar">
          <div
            v-if="currentPath"
            class="cursor-pointer"
            :class="{ 'link-active': currentProject || currentNews }"
            @click="
              () => {
                currentProject = null
                currentNews = null
              }
            "
          >
            {{ currentPath }}
          </div>
          <div v-if="currentProject || currentNews" class="q-mx-sm">/</div>
          <div v-if="currentProject" class="cursor-pointer link--active">{{ currentProject }}</div>
          <div v-if="currentNews" class="cursor-pointer link--active">{{ currentNews.title }}</div>
        </section>
        <section v-if="currentPath === 'Каталог проектов' && !currentProject" class="body">
          <section class="main-page__body main-page__body__header">
            <p>
              На данной странице опубликованы проекты, выполненные учащимися ГБОУ г. Москвы “Школа №1576” в ходе
              проектной деятельности и доступные для ознакомления. Проектная деятельность школьников — это
              познавательная, учебная, исследовательская и творческая деятельность, в результате которой появляется
              решение задачи, которое представлено в виде проекта. Проектно-исследовательская работа в школе — это
              новый, инновационный метод, соединяющий учебно-познавательный компонент, игровой, научный и творческий.
              Основное отличие такой деятельности для начальной школы - это то, что ученики, прежде всего, получают
              первые навыки исследования, благодаря чему развиваются специфические качества особого склада мышления.
              Всего в настоящий момент в данном разделе представлено 100500 проектов от 256 учащихся.
            </p>
            <div class="flex justify-between">
              <styled-input
                v-model="search"
                style="width: 500px; min-height: 30px"
                placeholder="Начните вводить текст..."
                @input="fetch"
              />
              <!-- <q-icon name="app:sort" class="cursor-pointer" size="md">
                <q-menu anchor="top left" self="bottom left">
                  <q-item clickable>
                    <q-item-section>Сортировать по названию</q-item-section>
                  </q-item>
                  <q-item clickable>
                    <q-item-section>Сортировать по дате добавления</q-item-section>
                  </q-item>
                  <q-item clickable>
                    <q-item-section>Сортировать по предметам</q-item-section>
                  </q-item>
                  <q-item clickable>
                    <q-item-section>Сортировать по ученикам</q-item-section>
                  </q-item>
                  <q-item clickable>
                    <q-item-section>Сортировать по учителям</q-item-section>
                  </q-item>
                </q-menu>
              </q-icon> -->
            </div>
          </section>
          <section class="main-page__body__content">
            <div v-for="project in projects" :key="project.name" class="project q-pb-md">
              <div class="flex justify-between q-mb-sm">
                <div style="font-size: 11px">{{ isoToLocalDate(project.createDate) }}</div>
                <div class="project__border" />
              </div>
              <div
                class="project__title q-mb-sm cursor-pointer"
                @click="
                  () => {
                    fetchProject(project.id)
                    currentProject = project.name
                  }
                "
              >
                {{ project.name }}
              </div>
              <div class="project__subjects q-mb-sm">
                {{ project.subjectAreas.map((x) => x.name).join('; ') }}
              </div>
              <div class="q-mb-sm">
                {{ project.annotation }}
              </div>
              <div class="flex justify-between">
                <div class="flex">
                  <div class="text-weight-bold q-mr-sm">Преподаватель:</div>
                  <div class="q-mr-lg">{{ '' }}</div>
                  <div class="text-weight-bold q-mr-sm">Ученик:</div>
                  <div>{{ project.learner ? project.learner.fullName : '' }}</div>
                </div>
                <div class="flex">
                  <div class="text-weight-bold q-mr-sm" q-mr-sm>Оценка:</div>
                  <div>{{ project.mark }}</div>
                </div>
              </div>
            </div>
          </section>
        </section>
        <section v-else-if="currentPath === 'Главная' && !currentNews">
          <!--          <section class="main-page__body main-page__body__header">-->
          <!--            {{currentPath}}-->
          <!--          </section>-->
          <section class="main-page__body__content">
            <div v-for="n in news" :key="n.title">
              <div class="flex justify-between q-my-sm q-mx-lg">
                <div style="font-size: 11px">{{ isoToLocalDate(n.dateTime) }}</div>
                <div class="project__border" />
              </div>
              <div class="news">
                <div class="news__img-block">
                  <q-img :src="n.image.url" style="height: 250px; width: 300px" />
                </div>
                <div class="news__text-block">
                  <div class="news__title q-my-sm" @click="currentNews = n">
                    {{ n.title }}
                  </div>
                  <div class="news__text q-my-md ellipsis-3-lines">
                    <div v-html="n.text" />
                  </div>
                  <styled-btn class="q-my-md" color="#484C51" label="Читать целиком" outline @click="currentNews = n" />
                </div>
              </div>
            </div>
          </section>
        </section>
        <section v-else-if="currentNews">
          <div class="news-block">
            <div class="news-block__wrapper">
              <div class="news-block__header">
                <q-img class="news-block__header__img" :src="currentNews.image.url" />
                <div class="flex column">
                  <div class="news-block__title">{{ currentNews.title }}</div>
                  <div class="news-block__author">{{ currentNews.author }}</div>
                </div>
              </div>
            </div>
            <div class="news-block__wrapper">
              <div class="news-block__content">
                <div v-html="currentNews.text" />
              </div>
            </div>
          </div>
        </section>
        <section v-else-if="currentProject" class="body">
          <div class="main-page__body main-page__body__header">
            <div class="flex justify-between q-mt-md">
              <div style="font-size: 11px">{{ isoToLocalDate(project.createDate) }}</div>
              <div class="project__border" />
            </div>
            <div
              class="project__title cursor-pointer"
              @click="
                () => {
                  fetchProject(project.id)
                }
              "
            >
              {{ project.name }}
            </div>
            <div class="project__subjects q-mb-sm">
              {{ project.subjectAreas.map((x) => x.title).join('; ') }}
            </div>
            <div class="flex">
              <div class="text-weight-bold q-mr-sm">Преподаватель:</div>
              <div class="q-mr-lg">
                {{
                  project.members.find((x) => x.type === MemberType.Teacher)
                    ? project.members.find((x) => x.type === MemberType.Teacher).fullName
                    : ''
                }}
              </div>
            </div>
            <div class="flex">
              <div class="text-weight-bold q-mr-sm">Ученик:</div>
              <div>{{ project.learner ? project.learner.fullName : '' }}</div>
            </div>
            <div
              v-for="expert in project.members.find((x) => x.type !== MemberType.Teacher) || []"
              :key="expert.fullName"
              class="flex"
            >
              <div class="text-weight-bold q-mr-sm">Эксперт:</div>
              <div>{{ expert.fillName }}</div>
            </div>
            <div class="flex">
              <div class="text-weight-bold q-mr-sm" q-mr-sm>Дата начала проекта:</div>
              <div>{{ isoToLocalDate(project.createDate) }}</div>
            </div>
            <div class="flex">
              <div class="text-weight-bold q-mr-sm" q-mr-sm>Оценка:</div>
              <div>{{ project.mark }}</div>
            </div>
          </div>
          <div class="main-page__body main-page__body__content">
            <div v-if="project.annotation">
              <div class="text-weight-bold q-my-md title">Аннотация</div>
              <div>{{ project.annotation }}</div>
            </div>
            <div v-if="project.researchTopic">
              <div class="text-weight-bold q-my-md title">Тема проекта</div>
              <div>{{ project.researchTopic }}</div>
            </div>

            <div v-if="project.researchPurpose">
              <div class="text-weight-bold q-my-md title">Цель</div>
              <div>{{ project.researchPurpose }}</div>
            </div>

            <div v-if="project.tasks">
              <div class="text-weight-bold q-my-md title">Задачи</div>
              <div v-for="(task, index) in project.tasks" :key="task.title" class="q-mt-sm">
                <div>{{ `${index + 1}. ${task.title}` }}</div>
                <div class="text-weight-bold q-my-sm">Результаты работы:</div>
                <div>{{ task.result }}</div>
                <div class="text-weight-bold q-my-sm">Вложения:</div>
                <div class="flex">
                  <file-input v-for="file in task.files" :key="file.fileId" class="flex" :file="file" read />
                </div>
              </div>
            </div>
            <div v-if="project.relevance">
              <div class="text-weight-bold q-my-md title">Актуальность</div>
              <div>{{ project.relevance }}</div>
            </div>
            <div v-if="project.workResult">
              <div class="text-weight-bold q-my-md title">Выводы</div>
              <div>{{ project.workResult }}</div>
            </div>
            <div class="text-weight-bold q-my-md title">Вложения</div>
            <div class="flex">
              <file-input v-for="file in project.resultFiles" :key="file.fileId" class="flex" :file="file" read />
            </div>
            <div v-if="project.explanatory">
              <div class="text-weight-bold q-my-md title">Пояснительная записка</div>
              <div>{{ project.explanatory }}</div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </q-scroll-area>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from '@vue/composition-api'
import { emptyProject } from 'components/project/emptyProject'
import ProjectApi from 'src/api/ProjectApi'
import { MemberType, ProjectDto, ProjectInfoDto } from 'src/models/Project'
import { handleGetError } from 'src/utils/errorHandler'
import { isoToLocalDate } from 'src/utils/isoToLocalDate'
import UserStore from 'src/store/modules/user'
import FileInput from 'components/base/FileInput.vue'
import { NewsDto } from 'src/models/News'
import NewsApi from 'src/api/NewsApi'

export default defineComponent({
  name: 'Main',
  components: { FileInput },
  setup() {
    const currentPath = ref<string | null>('Главная')
    const currentProject = ref<string | null>(null)
    const currentNews = ref<NewsDto | null>(null)
    const currentSort = ref<string | null>(null)

    const projects = ref<ProjectInfoDto[]>([])
    const project = ref<ProjectDto>(emptyProject)
    const search = ref('')

    const news = ref<NewsDto[]>([
      {
        title: '',
        dateTime: '',
        text: '',
        author: '',
        image: '',
      },
    ])

    const params = computed(() => {
      const params = new URLSearchParams()
      params.append('shared', 'true')
      if (search.value) params.append('name', search.value)
      if (currentSort.value) params.append('sortOrder', currentSort.value)
      return params
    })

    function fetch() {
      ProjectApi.filteredList(params.value)
        .then((payload) => (projects.value = payload))
        .catch(handleGetError)

      NewsApi.list()
        .then((payload) => (news.value = payload))
        .catch(handleGetError)
    }
    fetch()

    function fetchProject(id: number) {
      ProjectApi.getMainProject(id)
        .then((payload) => (project.value = payload))
        .catch(handleGetError)
    }

    return {
      search,
      projects,
      project,
      isoToLocalDate,
      MemberType,
      UserStore,
      currentPath,
      currentSort,
      currentNews,
      currentProject,
      news,

      fetchProject,
      fetch,
    }
  },
})
</script>

<style lang="scss" scoped>
.main-page {
  background: url(../../public/background.png);
  padding: 0 20px;

  &__inner-container {
    width: 1300px;
    background: #ffffff;
    box-shadow: 0px 3px 7px rgba(0, 0, 0, 0.5);
  }

  &__header {
    background-color: white;
    padding-top: 50px;
    text-align: center;
    font-weight: bold;
    font-size: 72px;
    text-transform: uppercase;
    color: #484c51;

    @media (max-width: 650px) {
      font-size: 36px;
    }

    @media (max-width: 370px) {
      font-size: 26px;
    }

    &--link {
      text-decoration: none;
    }

    &--sub {
      font-size: 36px;
      text-transform: uppercase;
      color: #9d9d9d;
    }

    &--dark {
      color: #484c51;
    }
  }

  &__nav-bar {
    padding: 8px 15px;
    display: flex;
    background: #f5f5f5;
    height: 35px;

    > a {
      font-size: 13px;
      display: flex;
      flex-direction: column;
      justify-content: center;

      &--active {
        color: #397ad0;
      }
    }
  }

  &__body {
    margin: 0 20px;

    &__header {
      > p {
        margin-top: 20px;
        font-size: 13px;
      }
    }

    &__content {
    }
  }
}

.project {
  margin: 20px 20px;

  &__border {
    display: flex;
    margin: 10px 0;
    width: calc(100% - 80px);
    background: #dfe7ec;
    height: 1px;
  }

  &__title {
    font-weight: bold;
    font-size: 21px;
    color: #397ad0;
  }

  &__subjects {
    font-size: 12px;
    color: #9f9f9f;
  }
}

.link-active {
  color: #397ad0;

  /*&--active {*/
  /*  color: #000;*/
  /*}*/
}

.title {
  font-size: 18px;
}

.main-header {
  width: 1300px;
  align-self: center;
  height: 50px;
  background-color: white;
  box-shadow: 10px 5px 5px #e0e0e0;
}

.news {
  padding: 30px;
  overflow: hidden;

  display: grid;
  grid-template-columns: 300px auto;
  grid-gap: 20px;

  @media (max-width: 600px) {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__title {
    font-weight: bold;
    font-size: 21px;
    color: #397ad0;
  }

  &__categories {
    font-size: 12px;
    color: #9f9f9f;
  }
}

.news-block {
  max-width: 1300px;
  margin: 20px 20px;
  &__title {
    font-weight: bold;
    font-size: 21px;
    color: #397ad0;
  }

  &__author {
    font-weight: bold;
    font-size: 14px;
  }

  &__wrapper {
    display: flex;
    justify-content: center;
    margin: 0 50px 50px;
  }

  &__header {
    max-width: 1000px;
    display: flex;
    justify-content: flex-start;

    @media (max-width: 600px) {
      display: flex;
      flex-direction: column;
      align-items: center;
    }

    > * {
      margin: 20px 20px 0 0;
    }

    &__img {
      height: 250px;
      width: 250px;
    }
  }

  &__content {
    max-width: 1000px;
    display: flex;
    justify-content: center;
  }
}
</style>
