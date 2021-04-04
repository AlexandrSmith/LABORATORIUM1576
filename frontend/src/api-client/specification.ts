export interface JsonApiError {
  id?: string
  status?: number
  code?: string
  title?: string
  detail?: string
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export interface JsonApiDocument<T = any> {
  data?: T
  errors?: JsonApiError[]
  meta?: any
}

export function createDocument(payload: unknown): JsonApiDocument {
  return { data: payload }
}

export default {}
