import { boot } from 'quasar/wrappers'
import PageName from 'src/router/names'
import UserStore from 'src/store/modules/user'

export default boot(({ router }) => {
  router.beforeEach((to, from, next) => {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
    if (to.matched.some((record) => record.meta.auth)) {
      if (!UserStore.loggedIn) {
        next({ name: PageName.MainPage, query: { redirect: to.fullPath } })
        return
      }
    } else if (UserStore.loggedIn && [PageName.Main].some((it) => it === to.name)) {
      next({ name: PageName.MainPage, query: { redirect: to.fullPath } })
      return
    }
    next()
  })
})
