<template>
  <div class="flex column">
    <div></div>
    <!-- <div
      class="full-width flex column justify-center absolute-top text-weight-bold"
      style="height: 50px; text-align: center; background-color: #ffdb9a"
    > -->
    <q-btn-dropdown
      flat
      class="menu-text flex justify-center column"
      style="height: 50px; text-align: center; background-color: #ffdb9a"
      icon="app:accounts"
      :label="`${members.length + 1} ${plural(members.length + 1, ['участник', 'участника', 'участников'])} `"
    >
      <q-list>
        <q-item v-close-popup clickable style="min-height: 20px">
          <q-item-section>
            <q-item-label>{{ learner.fullName }}</q-item-label>
          </q-item-section>
        </q-item>
        <q-item v-for="member in members" :key="member.id" v-close-popup clickable style="min-height: 20px">
          <q-item-section>
            <q-item-label>{{ member.fullName }}</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-btn-dropdown>
    <!-- </div> -->
    <q-scroll-area ref="scroll" class="full-height" style="max-height: calc(100% - 200px); margin-top: 50px">
      <div class="q-pa-md">
        <div v-for="(message, index) in messages" :key="index">
          <!--          <q-chat-message-->
          <!--            v-if="-->
          <!--              index === 0 ||-->
          <!--              (index > 0 && isoToLocalDate(message.dateTime) !== isoToLocalDate(messages[index - 1].dateTime))-->
          <!--            "-->
          <!--            :label="isoToLocalDate(message.dateTime)"-->
          <!--            bg-color="white"-->
          <!--            size="8"-->
          <!--          ></q-chat-message>-->
          <div v-if="message.fullName === 'Уведомление'" class="flex justify-center q-my-sm">
            <div class="notification">{{ message.text }}</div>
          </div>
          <q-chat-message
            v-else
            :text="[
              `
                <div class='flex justify-between q-mb-sm'>
                    <div style='${message.userId === UserStore.id ? 'color: #2F80ED;' : null}'>${message.fullName}</div>
                    <div style='color: #BDBDBD;'>${isoToLocalDateTime(message.dateTime)}</div>
                </div>
                <div>${message.text}</div>`,
            ]"
            :sent="message.userId === UserStore.id"
            bg-color="white"
            size="8"
          ></q-chat-message>
        </div>
      </div>
    </q-scroll-area>
    <div class="q-pa-md absolute-bottom" style="height: 150px">
      <styled-input v-model="message" label="Текст" />
      <div class="flex justify-between">
        <input
          ref="chatFile"
          class="hidden"
          type="file"
          @input="
            (event) => {
              if (event.target.files[0]) currentFile = event.target.files[0]
            }
          "
        />
        <!-- <styled-btn label="Прикрепить файл" outline @click="fileInputHandler" /> -->
        <span />
        <styled-btn label="Оставить комментарий" @click="sendMessageHandler" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, onUnmounted, PropType, ref, watch } from '@vue/composition-api'
import { QChatMessage, QScrollArea } from 'quasar'
import {
  isoToLocalDate,
  isoToLocalTime,
  localDateTimeToIso,
  localFormatDateTime,
  isoToLocalDateTime,
} from 'src/utils/isoToLocalDate'
import { format } from 'date-fns'
import { MemberDto, MessageDto, UserShortTableDto } from 'src/models/Project'
import UserStore from 'src/store/modules/user'
import ChatStore from 'src/store/modules/chat'
import plural from 'src/utils/plural'

export default defineComponent({
  name: 'ProjectChat',
  components: {
    QChatMessage,
  },
  props: {
    messages: {
      type: Array as PropType<MessageDto[]>,
      default: () => [],
    },
    members: {
      type: Array as PropType<MemberDto[]>,
      default: 1,
    },
    learner: {
      type: Object as PropType<UserShortTableDto>,
      default: () => null,
    },
    projectId: {
      type: Number,
    },
    drawerChat: Boolean,
  },
  setup(props, context) {
    const message = ref('')
    const scroll = ref<QScrollArea | null>(null)
    const chatFile = ref<HTMLInputElement | null>(null)
    const currentFile = ref<string | File | null>(null)

    function sendMessageHandler() {
      context.emit('new-message', {
        dateTime: new Date().toISOString(),
        text: message.value,
      })
      message.value = ''
      scrollDown()
    }

    function keyPressHandler(e: KeyboardEvent) {
      if (e.key === 'Enter') sendMessageHandler()
    }

    onMounted(() => {
      scrollDown()
      document.addEventListener('keypress', keyPressHandler, false)
    })

    onUnmounted(() => {
      document.removeEventListener('keypress', keyPressHandler, false)
    })

    function scrollDown() {
      scroll.value?.setScrollPosition(100 + props.messages.length * 80, 3)
    }

    function updateLastSeen() {
      const projectId = props.projectId
      const messageId = props.messages[props.messages.length - 1].id

      context.emit('new-message', {
        dateTime: new Date().toISOString(),
        text: 'release-notifications',
      })

      if (projectId && messageId) ChatStore.SET_LAST_SEEN({ projectId: projectId, messageId: messageId })
      if (scroll.value) scrollDown()
    }

    watch(
      () => props.messages,
      () => {
        if (props.drawerChat) {
          // updateLastSeen()
          scrollDown()
        }
      }
    )
    watch(
      () => props.drawerChat,
      () => {
        if (props.drawerChat) {
          // updateLastSeen()
          scrollDown()
        }
      }
    )

    function fileInputHandler() {
      if (chatFile) chatFile.value?.click()
    }

    return {
      message,
      UserStore,
      ChatStore,
      scroll,
      chatFile,
      currentFile,
      plural,
      format,
      localDateTimeToIso,
      isoToLocalDate,
      isoToLocalTime,
      localFormatDateTime,
      isoToLocalDateTime,

      scrollDown,
      fileInputHandler,
      sendMessageHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.notification {
  padding: 2px 12px;
  font-weight: 600;
  font-size: 12px;
  text-align: center;
  color: #2f80ed;
  background: rgba(0, 163, 255, 0.15);
  border-radius: 8px;
}
</style>
