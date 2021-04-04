import { ApiClient } from 'src/api-client/api-client'

export interface CrudInterface<TTable, TDto> {
  list: () => Promise<TTable[]>
  get: (id: number) => Promise<TDto>
  create: (dto: TDto) => Promise<number>
  update: (id: number, dto: TDto) => Promise<void>
  delete: (id: number) => Promise<void>
}

export function crudFactory<TTable, TDto>(client: ApiClient): CrudInterface<TTable, TDto> {
  return {
    list: () => client.get<TTable[]>(),
    get: (id: number) => client.get<TDto>(`${id}`),
    create: (dto: TDto) => client.post<number>(dto),
    update: (id: number, dto: TDto) => client.patch<void>(`${id}`, dto),
    delete: (id: number) => client.delete<void>(`${id}`),
  }
}
