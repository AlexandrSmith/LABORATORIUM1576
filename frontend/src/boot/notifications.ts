import { watch } from '@vue/composition-api'
import { useWebSocket } from '@vueuse/core'
import { Notify } from 'quasar'
import { boot } from 'quasar/wrappers'
import { Notification } from 'src/models/Notification'
import UserStore from 'src/store/modules/user'
import MenuStore from 'src/store/modules/menu'
import NotificationStore, { UserNotifications } from 'src/store/modules/notification'
import handleError, { handleGetError } from 'src/utils/errorHandler'
import wsUrl from 'src/utils/wsUrl'

export default boot(() => {
  const { data, open: openWs, close: closeWs } = useWebSocket(wsUrl('notifications'), {
    autoReconnect: true,
  })
  watch(data, (message: string) => {
    try {
      MenuStore.fetchProjects().catch(handleGetError)
      const userNotifications = JSON.parse(message) as UserNotifications[]

      if (Array.isArray(userNotifications)) {
        NotificationStore.FILL_NOTIFICATIONS(userNotifications)
        return
      }
      const notification = JSON.parse(message) as Notification
      Notify.create({
        message: `Проект ${notification.projectName}: ${notification.message}`,
        position: 'top-right',
        timeout: 10000,
        closeBtn: true,
      })
      NotificationStore.ADD_NOTIFICATION({
        project: notification.projectId,
        user: UserStore.info!.id,
        type: notification.type,
        quantity: 1,
      })
    } catch (e) {
      handleError(e)
    }
  })

  watch(
    () => UserStore.loggedIn,
    (loggedIn) => {
      if (loggedIn) {
        console.debug('open notification ws')
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        openWs()
      } else {
        console.debug('close notification ws')
        closeWs()
      }
    },
    { immediate: true }
  )
})
