import { getModule, Module, Mutation, VuexModule } from 'vuex-module-decorators'
import store from 'src/store'
import checkIfInStore from 'src/store/utils'

export interface LastSeen {
  projectId: number
  messageId: number
}

@Module({ name: 'chat', store, dynamic: true, namespaced: true, preserveState: checkIfInStore('chat') })
class ChatModule extends VuexModule {
  lastSeenIds: LastSeen[] = []

  get lastSeenId() {
    return this.lastSeenIds
  }

  @Mutation
  SET_LAST_SEEN(lastSeen: LastSeen) {
    const currentLastSeen = this.lastSeenIds.find((x) => x.projectId === lastSeen.projectId)
    if (currentLastSeen) this.lastSeenIds[this.lastSeenIds.indexOf(currentLastSeen)].messageId = lastSeen.messageId
    else this.lastSeenIds.push(lastSeen)
  }
}

const ChatStore = getModule(ChatModule)
export default ChatStore
