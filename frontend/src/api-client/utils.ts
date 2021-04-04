import { formatISO, isDate } from 'date-fns'

export function combineURLs(baseUrl: string, relativeUrl: string) {
  return relativeUrl ? `${baseUrl.replace(/\/+$/, '')}/${relativeUrl.replace(/^\/+/, '')}` : baseUrl
}

const isDateType = (value: unknown): value is Date => isDate(value)

export function paramsSerializer(params: Record<string, unknown>): string {
  const urlParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value != null) {
      if (Array.isArray(value)) {
        if (value.length) {
          urlParams.append(key, value.join(','))
          // value.forEach((x) => {
          //   urlParams.append(key, x)
          // })
        }
      } else if (isDateType(value)) {
        urlParams.append(key, formatISO(value))
      } else if (typeof value === 'string') {
        urlParams.append(key, value)
      } else if (typeof value === 'object') {
        urlParams.append(key, JSON.stringify(value))
      } else {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call,@typescript-eslint/no-unsafe-member-access
        urlParams.append(key, (value as any).toString())
      }
    }
  })

  return urlParams.toString()
}
