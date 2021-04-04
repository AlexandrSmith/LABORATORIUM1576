import { getCurrentInstance, Ref, ref } from '@vue/composition-api'
import { QDialog } from 'quasar'

export interface UseDialogInterface extends QDialog {
  dialogRef: Ref<QDialog | null>

  onOKClick(payload?: any): void
  onCancelClick(): void
  onDialogHide(): void
}

export default function useDialog() {
  const vm = getCurrentInstance()
  if (vm == null) {
    throw Error('Вызов useDialog не из setup')
  }
  const emit = vm.emit

  const dialogRef = ref<QDialog | null>(null)
  // following method is REQUIRED
  // (don't change its name --> "show")
  function show() {
    dialogRef.value?.show()
  }

  // following method is REQUIRED
  // (don't change its name --> "hide")
  function hide() {
    dialogRef.value?.hide()
  }

  function onDialogHide() {
    // required to be emitted
    // when QDialog emits "hide" event
    emit('hide')
  }

  function onOKClick(payload?: any) {
    // on OK, it is REQUIRED to
    // emit "ok" event (with optional payload)
    // before hiding the QDialog
    emit('ok', payload)
    // or with payload: this.$emit('ok', { ... })

    // then hiding dialog
    hide()
  }

  function onCancelClick() {
    // we just need to hide dialog
    hide()
  }

  return {
    // private api
    show,
    hide,
    // public api
    dialogRef,
    onOKClick,
    onCancelClick,
    onDialogHide,
  }
}
