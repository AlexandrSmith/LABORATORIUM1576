<template>
  <q-dialog ref="dialogRef" no-backdrop-dismiss @hide="onDialogHide">
    <q-card class="dialog" style="max-width: none">
      <q-card-section class="font-header-2 text-header text-center q-pb-none" style="max-width: 600px">
        <div class="header-title">
          <span>{{ fullName(clonedUser.surname) || '' }}</span>
        </div>
        <div class="dialog-section">
          <q-form @submit="okHandler">
            <styled-input v-model.trim="clonedUser.surname" required autofocus placeholder="Фамилия" />
            <styled-input v-model.trim="clonedUser.name" required placeholder="Имя" />
            <styled-input v-model.trim="clonedUser.patronymic" placeholder="Отчество" />
            <email-input v-model.trim="clonedUser.username" required placeholder="Email" />
            <styled-select v-model="clonedUser.role" :options="roles" emit-value map-options />
            <q-select
              v-model="clonedUser.areas"
              :options="subjects"
              style="margin-bottom: 10px"
              multiple
              emit-value
              map-options
              label="Предметная область"
            />
            <div class="flex justify-between q-mb-md">
              <styled-btn label="Отмена" @click="onCancelClick" />
              <div class="flex justify-end">
                <styled-btn v-if="user.id" class="q-mr-md" outline label="Удалить" @click="deleteBtnHandler" />
                <styled-btn label="Сохранить" type="submit" />
              </div>
            </div>
          </q-form>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import { defineComponent, PropType, ref } from '@vue/composition-api'
import { Role, UserDto } from 'src/models/User'
import { OptionValue } from 'src/models/Project'
import { translate } from 'src/utils/translate'
import { handleApiError } from 'src/utils/errorHandler'
import { fullName } from 'src/utils/fullName'
import { Dialog } from 'quasar'
import useDialog from 'components/common/useDialog'
import * as R from 'ramda'
import UserApi from 'src/api/UserApi'
import EmailInput from 'components/base/EmailInput.vue'

export default defineComponent({
  name: 'User',
  components: { EmailInput },
  props: {
    user: Object as PropType<UserDto>,
    subjects: Array as PropType<OptionValue[]>,
  },
  setup(props) {
    const dialog = useDialog()
    const roles = ref<OptionValue[]>(
      Object.keys(Role).map((x) => {
        return { label: translate(x), value: x }
      })
    )
    const clonedUser = ref(R.clone(props.user))

    function okHandler() {
      if (clonedUser.value) {
        if (clonedUser.value.id) {
          UserApi.update(clonedUser.value.id, clonedUser.value)
            .then(() => dialog.onOKClick())
            .catch(handleApiError)
        } else {
          UserApi.create(clonedUser.value)
            .then((id: number) => {
              clonedUser.value!.id = id
              dialog.onOKClick()
            })
            .catch(handleApiError)
        }
      }
    }

    function deleteBtnHandler() {
      Dialog.create({
        message: 'Вы уверены?',
        cancel: true,
      }).onOk(() => {
        if (props.user && props.user.id)
          UserApi.delete(props.user.id)
            .then(() => {
              dialog.onOKClick()
            })
            .catch(handleApiError)
      })
    }

    return {
      ...dialog,
      roles,
      clonedUser,
      fullName,

      okHandler,
      deleteBtnHandler,
    }
  },
})
</script>

<style lang="scss" scoped>
.dialog {
  width: 400px;
}
</style>
