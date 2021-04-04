<template>
  <div class="page">
    <project
      class="full-height"
      :project="fetchedProject"
      :is-teacher="UserStore.isTeacher"
      :is-expert="UserStore.isExpert"
      :is-student="UserStore.isStudent"
      @need-fetch="
        () => {
          $emit('need-fetch')
          MenuStore.fetchProjects()
          fetch()
        }
      "
      @deadline="deadlineHandler"
    />
    <q-btn
      v-if="!UserStore.isStudent && !UserStore.isExpert && fetchedProject.stage === 4"
      style="position: absolute; top: 61px; left: 290px"
      flat
      color="secondary"
      round
      dense
      size="lg"
      :icon="fetchedProject.shared ? 'app:share' : 'app:hide'"
      :disable="!fetchedProject.permissionToPublish"
      @click="shareHandler()"
    />
    <q-btn
      v-else-if="fetchedProject.stage === 4"
      style="position: absolute; top: 61px; left: 310px"
      flat
      color="secondary"
      round
      dense
      size="lg"
      :icon="fetchedProject.permissionToPublish ? 'app:share' : 'app:hide'"
      @click="sharePermissionHandler()"
    />
    <q-btn
      style="position: absolute; top: 61px; left: 260px"
      flat
      color="secondary"
      round
      dense
      size="lg"
      :icon="fetchedProject.locked ? 'app:locked' : 'app:unlocked'"
      :disable="!UserStore.isTeacher"
      @click="UserStore.isTeacher ? lockOrUnlockProject() : null"
    />
    <q-btn
      style="position: absolute; top: 61px"
      :style="drawerChat ? 'right: 415px' : 'right: 15px'"
      flat
      color="secondary"
      round
      dense
      size="lg"
      icon="app:comment"
      @click="
        () => {
          drawerChat = !drawerChat
          releaseNotificationsHandler()
        }
      "
    >
      <div class="text-red-10">
        {{
          NotificationStore.getNotifications.find((x) => x.project === projectId)
            ? NotificationStore.getNotifications.find((x) => x.project === projectId).quantity
            : ''
        }}
      </div>
    </q-btn>
    <q-drawer
      v-model="drawerChat"
      side="right"
      :value="true"
      content-class="side-menu-container column"
      :width="400"
      class="bg-side-menu"
    >
      <project-chat
        style="height: 100%"
        :messages="messages"
        :members="fetchedProject.members"
        :learner="fetchedProject.learner"
        :project-id="projectId"
        :drawer-chat="drawerChat"
        @new-message="newMessageHandler"
      />
    </q-drawer>
    <q-inner-loading :showing="loading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onUnmounted, ref, watch } from '@vue/composition-api'
import { MessageDto, ProjectDto } from 'src/models/Project'
import { handleApiError, handleGetError } from 'src/utils/errorHandler'
import { emptyProject, testProject } from 'components/project/emptyProject'
import ProjectApi from 'src/api/ProjectApi'
import Project from 'components/Project.vue'
import ProjectChat from 'components/ProjectChat.vue'
import UserStore from 'src/store/modules/user'
import ChatStore from 'src/store/modules/chat'
import wsUrl from 'src/utils/wsUrl'
import NotificationStore from 'src/store/modules/notification'
import MenuStore from 'src/store/modules/menu'

export default defineComponent({
  name: 'Projects',
  components: {
    Project,
    ProjectChat,
  },
  props: {
    projectId: {
      type: Number || String,
      default: null,
    },
  },
  setup(props) {
    const drawerChat = ref(true)

    const fetchedProject = ref<ProjectDto>(testProject)
    const messages = ref<MessageDto[]>([])
    const loading = ref(false)

    function fetch() {
      if (props.projectId) {
        loading.value = true
        ProjectApi.get(props.projectId)
          .then((payload) => {
            fetchedProject.value = payload
            loading.value = false
          })
          .catch(handleGetError)
      } else fetchedProject.value = emptyProject
    }
    fetch()

    function lockOrUnlockProject() {
      ProjectApi.lockOrUnlock(props.projectId, fetchedProject.value.locked ? !fetchedProject.value.locked : true)
        .then(fetch)
        .catch(handleGetError)
    }

    let firstData = false
    let webSocket = new WebSocket(wsUrl(`/chat/${props.projectId}`))

    function releaseNotificationsHandler() {
      webSocket.send(JSON.stringify({ text: 'release-notifications', dateTime: new Date().toISOString() }))
      NotificationStore.CLEAR_NOTIFICATIONS_FOR_PROJECT(props.projectId)
    }
    // const { data, open: openWs, close: closeWs } = useWebSocket(wsUrl('notifications'), {
    //   autoReconnect: true,
    // })
    // watch(data, () => {
    //   try {
    //     if (!firstData) {
    //       firstData = !firstData
    //       const firstPack: MessageDto[] = JSON.parse(data.value) as MessageDto[]
    //       messages.value = firstPack
    //       console.log('Messages: ', messages)
    //       return
    //     }
    //     const message: MessageDto = JSON.parse(data.value) as MessageDto
    //     console.log(message)
    //     if ('userId' in message) messages.value.push(message)
    //
    //   } catch (e) {
    //     handleError(e)
    //   }
    // })
    function connect() {
      webSocket.onopen = () => {
        webSocket.send(JSON.stringify({ text: 'release-notifications', dateTime: new Date().toISOString() }))
        NotificationStore.CLEAR_NOTIFICATIONS_FOR_PROJECT(props.projectId)
      }

      webSocket.onclose = () => {
        firstData = false
      }

      webSocket.onmessage = (evt) => {
        // console.log('evt: ', evt.data)
        if (!firstData) {
          firstData = !firstData
          const firstPack: MessageDto[] = JSON.parse(evt.data) as MessageDto[]
          messages.value = firstPack
          // console.log('Messages: ', messages)
          return
        }
        // console.log('message: ', evt)

        const message: MessageDto = JSON.parse(evt.data) as MessageDto
        // console.log('message: ', message)
        if ('userId' in message) messages.value.push(message)
      }
    }
    if (props.projectId) {
      connect()
    }

    function newMessageHandler(message: MessageDto) {
      webSocket.send(JSON.stringify(message))
    }

    onUnmounted(() => {
      webSocket.close()
    })

    watch(
      () => props.projectId,
      () => {
        messages.value = []
        drawerChat.value = true
        fetchedProject.value = emptyProject
        webSocket.close()
        fetch()
        webSocket = new WebSocket(wsUrl(`/chat/${props.projectId}`))
        connect()
      }
    )

    const lastSeenCounter = computed(() => {
      const lastSeenMessageId = ChatStore.lastSeenId.find((y) => y.projectId === props.projectId)?.messageId
      if (!lastSeenMessageId) return ''

      const lastSeenMessageInMessagesList = messages.value.find((x) => x.id === lastSeenMessageId)
      if (!lastSeenMessageInMessagesList) return ''

      const indexOfLastSeenMessage = messages.value.indexOf(lastSeenMessageInMessagesList)
      const lastSeenCount = messages.value.length - 1 - indexOfLastSeenMessage
      console.log('length: ', messages.value.length)
      return lastSeenCount > 0 ? lastSeenCount : ''
    })

    function deadlineHandler(date: string) {
      if (fetchedProject.value.id) {
        ProjectApi.setDeadline(fetchedProject.value.id, date).then(fetch).catch(handleApiError)
      }
    }

    function shareHandler() {
      if (fetchedProject.value.id) {
        ProjectApi.share(fetchedProject.value.id, !fetchedProject.value.shared ?? true)
          .then(fetch)
          .catch(handleApiError)
      }
    }

    function sharePermissionHandler() {
      if (fetchedProject.value.id) {
        ProjectApi.sharePermission(fetchedProject.value.id, !fetchedProject.value.permissionToPublish ?? true)
          .then(fetch)
          .catch(handleApiError)
      }
    }

    return {
      drawerChat,
      messages,
      loading,
      fetchedProject,
      NotificationStore,
      UserStore,
      ChatStore,
      MenuStore,

      fetch,
      lastSeenCounter,
      deadlineHandler,
      shareHandler,
      newMessageHandler,
      lockOrUnlockProject,
      releaseNotificationsHandler,
      sharePermissionHandler,
    }
  },
})
</script>

<style lang="scss" scoped></style>
