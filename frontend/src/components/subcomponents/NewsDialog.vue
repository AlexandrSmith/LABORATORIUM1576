<template>
  <q-dialog ref="dialogRef" no-backdrop-dismiss @hide="onDialogHide">
    <q-card class="dialog" style="max-width: none">
      <q-card-section class="font-header-2 text-header text-center q-pb-none" style="max-width: 600px">
        <div class="header-title">
          <span>{{ '' || '' }}</span>
        </div>
        <div class="dialog-section">
          <q-form @submit="okHandler">
            <div class="q-mb-md">
              <div class="full-width" style="display: grid; grid-template-columns: 4fr 1fr">
                <div class="q-pr-sm">
                  <styled-input ref="titleRef" v-model="news.title" required label="Заголовок" />
                  <styled-input v-model="news.author" required label="Автор" />
                </div>
                <div>
                  <q-img style="width: 160px; height: 160px; cursor: pointer" :src="file" @click="fileInputHandler" />
                  <!--                    @delete="deleteFileHandler"-->
                  <input ref="fileInput" type="file" style="display: none" @input="addFileHandler" />
                </div>
              </div>

              <q-editor v-model="news.text" class="q-mb-md"></q-editor>

              <div class="flex justify-between">
                <styled-btn label="Отмена" outline @click="onCancelClick" />
                <div class="flex justify-end">
                  <styled-btn v-if="newsId" class="q-mr-md" outline label="Удалить" @click="deleteBtnHandler" />
                  <styled-btn label="Сохранить" :disable="!news.image" type="submit">
                    <q-tooltip v-if="!news.image"> Необходимо загрузить изображение </q-tooltip>
                  </styled-btn>
                </div>
              </div>
            </div>
          </q-form>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from '@vue/composition-api'
import { handleApiError, handleGetError } from 'src/utils/errorHandler'
import { Dialog, QInput } from 'quasar'
import { NewsDto } from 'src/models/News'
import useDialog from 'components/common/useDialog'
import NewsApi from 'src/api/NewsApi'
import { localDateTimeToIso } from 'src/utils/isoToLocalDate'
import * as R from 'ramda'
import { format } from 'date-fns'

export default defineComponent({
  name: 'NewsDialog',
  components: {},
  props: {
    newsId: {
      type: Number,
      default: null,
    },
  },
  setup(props) {
    const dialog = useDialog()
    const fileInput = ref<HTMLInputElement | null>(null)
    const titleRef = ref<QInput | null>(null)

    const news = ref<NewsDto>({
      title: '',
      dateTime: '',
      text: '',
      author: '',
      image: '',
    })

    onMounted(() => {
      news.value.image = undefined
    })

    function fetch() {
      if (props.newsId) {
        NewsApi.get(props.newsId)
          .then((payload) => {
            news.value = payload
          })
          .catch(handleGetError)
      }
    }
    fetch()

    function fileInputHandler() {
      fileInput.value?.click()
    }

    function deleteFileHandler() {
      news.value.image = undefined
    }

    function addFileHandler(event: InputEvent) {
      const target = event.target as HTMLInputElement
      if (target && target.files && target.files[0]) {
        const file = target.files[0]
        //
        // const newFile: Partial<FileDto> = {
        //   projectId: props.project.id || 0,
        //   taskId: currentTaskNumber.value,
        //   file: typeof file === 'string' ? undefined : file,
        // }
        news.value.image = typeof file === 'string' ? undefined : file
      }
    }

    function dateToDateTimeString(date: Date) {
      return format(date, 'dd.MM.yyyy HH:mm')
    }

    function okHandler() {
      const newNews: Partial<NewsDto> = {
        title: news.value.title,
        dateTime: news.value.dateTime,
        text: news.value.text,
        author: news.value.author,
        image: news.value.image,
      }
      newNews.dateTime = localDateTimeToIso(dateToDateTimeString(new Date())) || ''
      if (props.newsId) {
        NewsApi.update(props.newsId, newNews).then(dialog.onOKClick).catch(handleApiError)
      } else {
        NewsApi.create(newNews).then(dialog.onOKClick).catch(handleApiError)
      }
    }

    function deleteBtnHandler() {
      Dialog.create({
        message: 'Вы уверены?',
        cancel: true,
      }).onOk(() => {
        if (props.newsId) NewsApi.delete(props.newsId).then(dialog.onOKClick).catch(handleApiError)
      })
    }

    const file = computed(() => {
      return news.value.image
        ? typeof news.value.image === 'string'
          ? news.value.image
          : 'url' in news.value.image
          ? news.value.image.url
          : URL.createObjectURL(news.value.image)
        : 'image-add.png'
    })

    return {
      ...dialog,
      news,
      fileInput,
      file,
      titleRef,

      okHandler,
      deleteBtnHandler,
      deleteFileHandler,
      addFileHandler,
      fileInputHandler,
    }
  },
})
</script>

<style lang="scss" scoped></style>
