<template>
  <div class="page">
    <div class="text-weight-bold q-mb-md">Информация</div>
    <div class="flex">
      <div class="text-weight-medium q-mr-sm">Почта:</div>
      <div class="q-mb-md">{{ user.username }}</div>
    </div>

    <q-form @submit="update">
      <styled-input v-model.trim="user.surname" required autofocus placeholder="Фамилия" @input="updated = true" />
      <styled-input v-model.trim="user.name" required placeholder="Имя" @input="updated = true" />
      <styled-input v-model.trim="user.patronymic" placeholder="Отчество" @input="updated = true" />
      <div class="flex justify-end">
        <styled-btn label="Сохранить" :disable="!updated" type="submit" />
      </div>
    </q-form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import UserStore from 'src/store/modules/user'
import { UserDto } from 'src/models/User'
import UserApi from 'src/api/UserApi'
import { handleApiError, handleGetError } from 'src/utils/errorHandler'

export default defineComponent({
  name: 'PersonalArea',
  setup() {
    const updated = ref(false)
    const user = ref<UserDto | null>(null)

    ;(function () {
      if (UserStore.id)
        UserApi.get(UserStore.id)
          .then((payload) => {
            user.value = payload
            console.log(user.value)
          })
          .catch(handleGetError)
    })()

    function update() {
      if (user.value && user.value.id)
        UserApi.update(user.value.id, user.value)
          .then(() => (updated.value = false))
          .catch(handleApiError)
    }

    return {
      user,
      updated,

      update,
      // fullName: computed(() => UserStore.info?.fullName),
      // username: computed(() => UserStore.info?.username),
    }
  },
})
</script>

<style lang="scss" scoped></style>
