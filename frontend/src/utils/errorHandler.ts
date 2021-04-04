import { AxiosError } from 'axios'
import { StatusCodes } from 'http-status-codes'
import { Notify } from 'quasar'
import { JsonApiDocument, JsonApiError } from 'src/api-client/specification'

export function getSimpleErrorApiText(
  err: AxiosError<JsonApiDocument<JsonApiError>>,
  defaultMessage = 'Неизвестная ошибка'
) {
  return err?.response?.data?.errors?.[0]?.detail ?? err?.response?.data?.errors?.[0]?.title ?? defaultMessage
}

export default function handleError(error: Error | any) {
  if (!error) {
    console.error('error', error)
    return
  }
  if (error instanceof Error) {
    // eslint-disable-next-line no-console
    console.error('handleError', error.message)
  } else {
    // eslint-disable-next-line no-console
    console.error('handleError', error)
  }
}

export function handleJsonApiErrors(errors: JsonApiError[]) {
  errors.forEach((error) => {
    handleError(`JsonApiError: ${JSON.stringify(error)}`)
  })
}

export function createHandlerApiError(message: string) {
  return function handler(err: AxiosError<JsonApiDocument>) {
    handleError(err)

    if (err.response?.status === StatusCodes.UNAUTHORIZED) {
      return
    }
    let msg = err.response?.data?.errors?.[0]?.detail ?? err.response?.data?.errors?.[0]?.title
    if (msg === undefined || msg === '') {
      msg = message
    }
    Notify.create(msg)
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function handleApiError(err: AxiosError) {
  createHandlerApiError('Произошла ошибка при сохранении')(err)
}

export function handleGetError(err: AxiosError) {
  createHandlerApiError('Произошла ошибка при загрузке данных')(err)
}
