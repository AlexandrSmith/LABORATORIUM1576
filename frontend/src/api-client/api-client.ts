/* eslint-disable @typescript-eslint/no-explicit-any,@typescript-eslint/no-unsafe-call,@typescript-eslint/no-unsafe-member-access,@typescript-eslint/no-unsafe-return,@typescript-eslint/no-unsafe-assignment */
import axios, { AxiosPromise, AxiosRequestConfig, AxiosResponse } from 'axios'
import httpCodes from 'http-status-codes'
import { combineURLs, paramsSerializer } from 'src/api-client/utils'
import { createDocument, JsonApiDocument } from './specification'

const defaultApiUrl = process.env.SERVER ? combineURLs(process.env.APP_URL || '', 'api') : '/api'

const { API_URL = defaultApiUrl } = process.env

export const axiosClient = axios.create({
  baseURL: API_URL,
  withCredentials: true,
  paramsSerializer: paramsSerializer,
})

// преобразованный ответ, избавленный от обертки в виде data: {}
type JsonApiResponse<T> = T
type JARPromise<T> = Promise<JsonApiResponse<T>>

type JsonApiFullResponse<T = any> = AxiosResponse<JsonApiDocument<T>>

export function jsonApiFormat(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
  const originalMethod = descriptor.value
  // eslint-disable-next-line no-param-reassign
  descriptor.value = function jsonApiFormatDecorator(paramOne: string | unknown, paramTwo: unknown, ...rest: any[]) {
    const newArgs = [paramOne, paramTwo, ...rest]
    if (typeof paramOne === 'object') {
      newArgs[0] = createDocument(paramOne)
    } else {
      newArgs[1] = createDocument(paramTwo)
    }
    return originalMethod.apply(this, newArgs)
  }
  return descriptor
}

async function handleResponse<T = any>(requestPromise: AxiosPromise<T>) {
  // const context: ApiClient = this;
  const response: JsonApiFullResponse = await requestPromise
  // TODO сделать проверку на формат документа или оставить так
  // let url = context.baseUrl;
  // if (typeof args[0] === 'string') {
  //   url = combineURLs(url, args[0]);
  // }
  // console.log(`${propertyKey} ${url} args: ${JSON.stringify(args)} response: ${JSON.stringify(data)}`);
  if (response.status === httpCodes.NO_CONTENT) {
    return null
  }
  const result = response.data?.data
  if (result === undefined) {
    throw new Error('No data field in response')
  }
  return result
}

export function convertToFormData(data: Record<string, any>): FormData {
  const formData = new FormData()
  Object.entries(data).forEach(([key, value]) => {
    console.log(key, value)
    if (value != null) {
      formData.append(key, value)
    }
  })
  return formData
}

// noinspection JSUnusedGlobalSymbols
export class ApiClient {
  readonly baseUrl: string

  constructor(baseUrl = '') {
    this.baseUrl = baseUrl
  }

  get<T = any>(): JARPromise<T>
  get<T = any>(params?: unknown): JARPromise<T>
  get<T = any>(url: string, params?: unknown, config?: AxiosRequestConfig): JARPromise<T>
  get<T = any>(paramOne?: string | unknown, paramTwo?: unknown, config?: AxiosRequestConfig): JARPromise<T> {
    const { url, data } = this.parseArgs(paramOne, paramTwo)
    return handleResponse<T>(axiosClient.get(url, { params: data, ...(config ?? {}) }))
  }

  post<T = any>(data: unknown): JARPromise<T>
  post<T = any>(url: string, data?: unknown): JARPromise<T>
  post<T = any>(url: string, data: unknown, params: unknown): JARPromise<T>
  @jsonApiFormat
  post<T = any>(
    paramOne: string | unknown,
    paramTwo?: unknown,
    params?: unknown,
    config: AxiosRequestConfig = {}
  ): JARPromise<T> {
    const { url, data } = this.parseArgs(paramOne, paramTwo)
    return handleResponse<T>(axiosClient.post(url, data, { params, ...config }))
  }

  postMultiPart<T = any>(url: string, data: Record<string, unknown>): JARPromise<T> {
    const formData = convertToFormData(data)
    return handleResponse<T>(
      axiosClient.post(combineURLs(this.baseUrl, url), formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
    )
  }

  patch<T = any>(data: unknown): JARPromise<T>
  patch<T = any>(url: string, data?: unknown): JARPromise<T>
  patch<T = any>(url: string, data: unknown, params: unknown): JARPromise<T>
  @jsonApiFormat
  patch<T = any>(paramOne: string | unknown, paramTwo?: unknown, params?: unknown): JARPromise<T> {
    const { url, data } = this.parseArgs(paramOne, paramTwo)
    return handleResponse<T>(axiosClient.patch(url, data, { params }))
  }

  patchMultiPart<T = any>(url: string, data: Record<string, unknown>): JARPromise<T> {
    const formData = convertToFormData(data)
    return handleResponse<T>(
      axiosClient.patch(combineURLs(this.baseUrl, url), formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
    )
  }

  put<T = any>(data: unknown): JARPromise<T>
  put<T = any>(url: string, data?: unknown): JARPromise<T>
  @jsonApiFormat
  put<T = any>(paramOne: string | unknown, paramTwo?: unknown): JARPromise<T> {
    const { url, data } = this.parseArgs(paramOne, paramTwo)
    return handleResponse<T>(axiosClient.put(url, data))
  }

  delete<T = void>(data: unknown): JARPromise<T>
  delete<T = void>(url: string, data?: unknown, config?: AxiosRequestConfig): JARPromise<T>
  @jsonApiFormat
  async delete(paramOne: string | unknown, paramTwo?: unknown, config?: AxiosRequestConfig): Promise<any> {
    const { url, data } = this.parseArgs(paramOne, paramTwo)
    return handleResponse(axiosClient.delete(url, { data, ...(config ?? {}) }))
  }

  async request(url: string, config: AxiosRequestConfig) {
    return axiosClient.request({
      url: combineURLs(this.baseUrl, url),
      ...config,
    })
  }

  async upload(url: string, file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return axiosClient.post(combineURLs(this.baseUrl, url), formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  }

  private parseArgs(paramOne?: string | unknown, paramTwo?: unknown) {
    let url = this.baseUrl
    let data = paramTwo
    if (typeof paramOne === 'string') {
      url = combineURLs(this.baseUrl, paramOne)
    } else if (paramOne === undefined) url = this.baseUrl
    else {
      data = paramOne
    }
    return { url, data }
  }
}

const createApiClient = (baseUrl = '') => new ApiClient(baseUrl)

export default createApiClient
