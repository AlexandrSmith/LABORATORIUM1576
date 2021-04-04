import { combineURLs } from 'src/api-client/utils'

const wsBaseUrl = `${location.protocol.replace('http', 'ws')}//${location.host}/ws`

export default function wsUrl(path: string): string {
  return combineURLs(wsBaseUrl, path)
}
