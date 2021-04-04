import { getModule, Module, Mutation, VuexModule } from 'vuex-module-decorators'
import store from 'src/store'
import checkIfInStore from 'src/store/utils'
import { NotificationType } from 'src/models/Notification'

export interface UserNotifications {
  project: number
  user: number
  type: NotificationType
  quantity: number
}

@Module({ name: 'notification', store, dynamic: true, namespaced: true, preserveState: checkIfInStore('notification') })
class NotificationModule extends VuexModule {
  notifications: UserNotifications[] = []

  get getNotifications() {
    return this.notifications
  }

  @Mutation
  ADD_NOTIFICATION(notification: UserNotifications) {
    const sameProjNotifications = this.notifications.find((x) => x.project === notification.project)
    if (!sameProjNotifications) {
      this.notifications.push(notification)
      return
    }
    const notificationIndex = this.notifications.indexOf(sameProjNotifications)
    if (notificationIndex) {
      const oldNotification = this.notifications[notificationIndex]
      oldNotification.quantity += 1
      this.notifications.splice(notificationIndex, 1, oldNotification)
    } else return
  }

  @Mutation
  CLEAR_NOTIFICATIONS_FOR_PROJECT(projectId: number) {
    this.notifications = this.notifications.filter((x) => x.project !== projectId)
  }

  @Mutation
  FILL_NOTIFICATIONS(oldNotifications: UserNotifications[]) {
    this.notifications = oldNotifications
  }
}

const NotificationStore = getModule(NotificationModule)
export default NotificationStore
