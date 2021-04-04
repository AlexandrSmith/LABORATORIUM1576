<template>
  <div class="page flex justify-center column relative-position">
    <styled-btn
      class="absolute-top-right"
      style="z-index: 1; margin-top: 28px; margin-right: 24px"
      round
      dense
      flat
      icon="app:plus-circle"
      @click="addUserHandler"
    />

    <styled-table
      label="Пользователи"
      hide-bottom
      style="height: calc(100vh - 100px)"
      :columns="[
        { name: 'fullName', label: 'ФИО', field: (row) => fullName(row) },
        { name: 'username', label: 'Email', field: 'username' },
        { name: 'role', label: 'Роль', field: (row) => translate(row.role) },
        { name: 'areas', label: 'Предметные области', field: (row) => areas(row) },
      ]"
      :data="users"
      @row-click="(row) => editUserHandler(row)"
    />

    <q-inner-loading :showing="loading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import { Role, UserDto, UserStatus } from 'src/models/User'
import { translate } from 'src/utils/translate'
import { handleApiError, handleGetError } from 'src/utils/errorHandler'
import { OptionValue } from 'src/models/Project'
import { Dialog } from 'quasar'
import { fullName } from 'src/utils/fullName'
import UserApi from 'src/api/UserApi'
import User from 'components/subcomponents/User.vue'
import ProjectApi from 'src/api/ProjectApi'
import StyledTable from 'components/base/StyledTable.vue'

export default defineComponent({
  name: 'Subjects',
  components: {
    StyledTable,
  },
  setup() {
    const users = ref<UserDto[]>([])
    const loading = ref(false)
    const subjects = ref<OptionValue[]>([])

    function fetchSubjects() {
      loading.value = true
      ProjectApi.subjects
        .list()
        .then((payload) => {
          subjects.value = payload.map((x) => {
            return { value: x.id || 0, label: x.title }
          })
          loading.value = false
        })
        .catch(handleGetError)
    }
    fetchSubjects()

    function fetch() {
      loading.value = true
      UserApi.list()
        .then((payload) => {
          users.value = payload
          loading.value = false
        })
        .catch(handleGetError)
    }
    fetch()

    function editUserHandler(user: UserDto) {
      Dialog.create({
        component: User,
        user: user,
        subjects: subjects.value,
      }).onOk(() => {
        fetch()
      })
    }

    function addUserHandler() {
      const newUser = {
        username: '',
        password: '',
        surname: '',
        name: '',
        status: UserStatus.Active,
        role: Role.Teacher,
        areas: [],
      }
      Dialog.create({
        component: User,
        user: newUser,
        subjects: subjects.value,
      }).onOk(() => {
        fetch()
      })
    }

    return {
      users,
      loading,
      subjects,
      translate,

      areas(user: UserDto) {
        return user.areas.length > 0
          ? user.areas.map((area) => subjects.value.find((subj) => subj.value === area)?.label).join('; ')
          : 'Предметные области'
      },

      fullName,
      editUserHandler,
      addUserHandler,
    }
  },
})
</script>

<style lang="scss" scoped></style>
