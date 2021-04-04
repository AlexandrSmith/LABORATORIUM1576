<template>
  <div class="page flex justify-center column">
    <h5 class="text-weight-bold">Предметные области</h5>
    <div v-for="(subject, index) in subjectAreas" :key="subject.title" class="subject-block flex justify-between">
      <div class="flex column justify-center">{{ subject.title }}</div>
      <q-btn icon="app:minus-circle" flat round @click="deleteSubjectAreaHandler(index)" />
    </div>
    <styled-btn class="q-my-md" label="Добавить предметную область" @click="newSubjectAreaHandler" />
    <q-inner-loading :showing="loading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import { SubjectAreaDto } from 'src/models/Project'
import { Dialog } from 'quasar'
import { handleApiError, handleGetError } from 'src/utils/errorHandler'
import NewSubjectDialog from 'components/subcomponents/NewSubjectDialog.vue'
import ProjectApi from 'src/api/ProjectApi'

export default defineComponent({
  name: 'Subjects',
  setup() {
    const subjectAreas = ref<SubjectAreaDto[]>([])
    const loading = ref(false)

    function fetch() {
      loading.value = true
      ProjectApi.subjects
        .list()
        .then((payload) => {
          subjectAreas.value = payload
          loading.value = false
        })
        .catch(handleGetError)
    }
    fetch()

    function newSubjectAreaHandler() {
      Dialog.create({
        component: NewSubjectDialog,
      }).onOk((title: string) => {
        loading.value = true
        ProjectApi.subjects
          .create({ id: 0, title: title, description: '' })
          .then(() => fetch())
          .catch(handleApiError)
      })
    }

    function deleteSubjectAreaHandler(index: number) {
      const subject = subjectAreas.value[index]
      if (subject.id) {
        loading.value = true
        ProjectApi.subjects
          .delete(subject.id)
          .then(() => fetch())
          .catch(handleApiError)
      }
    }

    return {
      loading,
      subjectAreas,

      newSubjectAreaHandler,
      deleteSubjectAreaHandler,
    }
  },
})
</script>

<style lang="scss" scoped></style>
