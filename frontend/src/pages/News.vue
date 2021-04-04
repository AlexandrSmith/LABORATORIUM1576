<template>
  <div class="page flex justify-center column">
    <h5 class="text-weight-bold">Новости</h5>
    <div
      v-for="n in news"
      :key="n.id"
      class="subject-block flex justify-between q-mb-md"
      @click="openNewsHandler(n.id)"
    >
      <div class="flex">
        <q-img class="q-mr-md" width="100px" height="100px" :src="n.image.url" />
        <div class="column">
          <div class="title">{{ n.title }}</div>
          <div class="author">{{ n.author }}</div>
        </div>
      </div>
      <q-btn icon="app:minus-circle" flat round @click.stop="deleteSubjectAreaHandler(n.id)" />
    </div>
    <styled-btn class="q-my-md" label="Добавить новость" @click="newNewsHandler" />
    <q-inner-loading :showing="loading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import { handleApiError, handleGetError } from 'src/utils/errorHandler'
import { NewsDto } from 'src/models/News'
import NewsApi from 'src/api/NewsApi'
import { Dialog } from 'quasar'
import NewsDialog from 'components/subcomponents/NewsDialog.vue'

export default defineComponent({
  name: 'News',
  setup() {
    const news = ref<NewsDto[]>([])
    const loading = ref(false)

    function fetch() {
      loading.value = true
      NewsApi.list()
        .then((payload) => (news.value = payload))
        .catch(handleGetError)
        .finally(() => (loading.value = false))
    }
    fetch()

    function newNewsHandler() {
      Dialog.create({
        component: NewsDialog,
      }).onOk(fetch)
    }

    function deleteSubjectAreaHandler(id: number) {
      Dialog.create({
        message: 'Вы уверены?',
        cancel: true,
      }).onOk(() => {
        NewsApi.delete(id).then(fetch).catch(handleApiError)
      })
    }

    function openNewsHandler(id: number) {
      console.log(id)
      Dialog.create({
        component: NewsDialog,
        newsId: id,
      }).onOk(fetch)
    }

    return {
      loading,
      news,

      newNewsHandler,
      openNewsHandler,
      deleteSubjectAreaHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.title {
  font-weight: bold;
  font-size: 21px;
  color: #397ad0;
}

.author {
  font-weight: bold;
  font-size: 14px;
}
</style>
