<template>
  <q-card class="login-form" flat>
    <div v-if="close" class="row justify-end q-pt-xs q-pr-xs">
      <q-btn icon="app:close" size="12px" dense flat @click.stop="$emit('close')" />
    </div>
    <q-card-section>
      <q-form class="q-gutter-y-sm" @submit.prevent="onPasswordLogin">
        <email-input v-model="formData.username" required no-required-decorations />
        <styled-input
          v-model="formData.password"
          label="Пароль"
          :type="isPwd ? 'password' : 'text'"
          required
          required-msg="Необходимо ввести пароль"
          no-required-decorations
          :error="!!errorMessage"
          :error-message="errorMessage"
        >
          <template #append>
            <q-icon :name="isPwd ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="isPwd = !isPwd" />
          </template>
        </styled-input>
        <styled-btn label="Войти" class="full-width" type="submit" :loading="loading" />
        <div class="row justify-center q-mt-lg">
          <styled-btn label="Войти с помощью" icon-right="app:microsoft" no-caps outline @click="microsoftLogin" />
        </div>
      </q-form>
    </q-card-section>
  </q-card>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from '@vue/composition-api'
import { AxiosError } from 'axios'
import EmailInput from 'components/base/EmailInput.vue'
import PageName from 'src/router/names'
import useRoute from 'src/router/useRoute'
import useRouter from 'src/router/useRouter'
import UserStore from 'src/store/modules/user'
import handleError, { getSimpleErrorApiText } from 'src/utils/errorHandler'

export default defineComponent({
  name: 'LoginForm',
  components: { EmailInput },
  props: {
    redirect: Boolean,
    close: Boolean,
  },
  emits: ['success'],
  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()

    function handleLogInResult() {
      if (props.redirect) {
        let redirectUrl = route.query.redirect as string
        let redirect
        if (redirectUrl == undefined) {
          redirect = {
            name: PageName.PersonalArea,
          }
        } else {
          redirect = redirectUrl
        }
        router.push(redirect).catch(handleError)
      } else {
        emit('success')
      }
    }

    const formData = reactive<{
      username: string
      password: string
    }>({
      username: '',
      password: '',
    })

    const isPwd = ref(true)
    const loading = ref(false)

    const errorMessage = ref('')
    const handleErrorToText = (err: AxiosError) => (errorMessage.value = getSimpleErrorApiText(err))

    function onPasswordLogin() {
      loading.value = true
      void UserStore.passwordLogIn({
        username: formData.username,
        password: formData.password,
      })
        .then(handleLogInResult)
        .catch(handleErrorToText)
        .finally(() => (loading.value = false))
    }

    return {
      formData,
      isPwd,
      errorMessage,
      loading,

      onPasswordLogin,

      microsoftLogin: () => UserStore.oauthLogIn('microsoft'),
    }
  },
})
</script>

<style lang="scss" scoped>
.login-form {
  min-width: 300px;
  margin: 0 auto;
}
</style>
