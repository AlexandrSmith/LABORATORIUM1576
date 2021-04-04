import { startsWith } from 'ramda'
import toDataUrl from 'src/utils/toDataUrl'

export default function blobUrlToDataUrl(blobUrl: string) {
  return fetch(blobUrl, {
    method: 'GET',
  })
    .then((response) => response.blob())
    .then((blob) => toDataUrl(blob))
}

export async function mapBlobs(url: string) {
  if (startsWith('blob', url)) {
    const dataUrl = await blobUrlToDataUrl(url)
    if (dataUrl) {
      URL.revokeObjectURL(url)
    }
    return dataUrl || ''
  }
  return url
}
