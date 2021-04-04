import { StatusCodes } from 'http-status-codes'
import { Notify } from 'quasar'
import { axiosClient } from 'src/api-client/api-client'
import { AxiosError } from 'axios'
import { JsonApiError } from 'src/api-client/specification'
import PageName from 'src/router/names'
import UserStore from 'src/store/modules/user'
import handleError, { handleJsonApiErrors } from 'src/utils/errorHandler'
import { boot } from 'quasar/wrappers'

export default boot(({ router }) => {
  axiosClient.interceptors.response.use(undefined, async (error: AxiosError) => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    const jsonApiErrors = error.response?.data.errors as JsonApiError[]

    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    const authRequired = router.currentRoute.matched.some((record) => record.meta.auth)

    // TODO refactor this
    if (error.response?.status === StatusCodes.UNAUTHORIZED) {
      if (authRequired && UserStore.loggedIn) {
        await UserStore.logOut()
          .catch(handleError)
          .finally(() => {
            if (router.currentRoute.name !== PageName.Main) {
              Notify.create({
                message: 'Ваша сессия истекла. Введите логин и пароль.',
              })
              router.push({ name: PageName.Main, query: { redirect: router.currentRoute.fullPath } }).catch((e) => {
                handleError(e)
              })
            }
          })
      }
      // throw new Error('Не авторизованный пользователь');
    }
    if (jsonApiErrors) {
      handleJsonApiErrors(jsonApiErrors)
    }
    throw error
  })
})
