import createApiClient from './api-client'
// import { JsonApiDocument, JsonApiError } from './specification'

export default createApiClient
// export {
//   JsonApiError,
//   JsonApiDocument
// }

export interface ExportFile {
  name: string
  uuid: string
}

export function handleDownload(data: ExportFile) {
  const link = document.createElement('a')
  link.href = `/api/download/${data.uuid}/${data.name}`
  link.setAttribute('download', '') //any other extension
  if (typeof link.download === 'undefined') {
    link.setAttribute('target', '_blank')
  }
  document.body.appendChild(link)
  link.click()
  link.remove()
}
