import { Dialog, DialogChainObject, QDialogOptions } from 'quasar'

export default function useConfirmationDialog(message: string | QDialogOptions, title?: string): DialogChainObject {
  const opts: QDialogOptions = typeof message === 'string' ? { message, title } : message
  return Dialog.create({
    ok: 'Да',
    cancel: 'Нет',
    ...opts,
  })
}
