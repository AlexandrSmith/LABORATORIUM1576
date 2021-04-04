import { FileInfo } from 'src/models/Project'

export function downloadHandler(file: FileInfo) {
  const downloadLink = document.createElement('a')
  downloadLink.download = file.filename
  downloadLink.href = file.url
  document.body.appendChild(downloadLink)
  downloadLink.click()
  document.body.removeChild(downloadLink)
}
