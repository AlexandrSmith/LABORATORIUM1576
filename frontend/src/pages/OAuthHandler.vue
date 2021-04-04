<template>
  <q-page padding class="row justify-center items-center">
    <q-card flat class="column justify-center items-center" style="font-size: 1.2rem">
      <template v-if="error">
        <q-icon size="128px" name="app:sad" />
        <q-card-section>При аутентификации произошла ошибка</q-card-section>
        <q-card-section>{{ error }}</q-card-section>
        <q-card-actions>
          <styled-btn label="На главную" to="/" />
        </q-card-actions>
      </template>
      <template v-else>
        <q-spinner-gears size="128px" color="primary" />
        <q-card-section> Идет аутентификация...</q-card-section>
      </template>
    </q-card>
  </q-page>
</template>

<script lang="ts">
import { defineComponent, PropType, ref } from '@vue/composition-api'
import { AxiosError } from 'axios'
import { OAuthProvider } from 'src/api/AuthApi'
import PageName from 'src/router/names'
import useRoute from 'src/router/useRoute'
import useRouter from 'src/router/useRouter'
import UserStore from 'src/store/modules/user'
import handleError, { getSimpleErrorApiText } from 'src/utils/errorHandler'

export default defineComponent({
  name: 'OAuthHandler',
  props: {
    provider: {
      type: String as PropType<OAuthProvider>,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter()
    const data = useRoute().query
    const error = ref('')

    UserStore.handleOAuthCallback({
      provider: props.provider,
      payload: {
        state: data.state,
        code: data.code,
      },
    })
      .then(() => {
        router.push({ name: PageName.Topics }).catch(handleError)
      })
      .catch((e: AxiosError) => {
        error.value = getSimpleErrorApiText(e)
      })

    return {
      error,
    }
  },
})
</script>

<style lang="scss" scoped></style>
