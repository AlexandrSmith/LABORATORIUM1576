import createApiClient from 'src/api-client'
import { NewsDto } from 'src/models/News'

const client = createApiClient('/news')

export default {
  list: () => client.get<NewsDto[]>(),
  get: (id: number) => client.get<NewsDto>(`${id}`),
  update: (id: number, dto: Partial<NewsDto>) => client.patchMultiPart(`${id}`, dto),
  create: (dto: Partial<NewsDto>) => client.postMultiPart('', dto),
  delete: (id: number) => client.delete(`${id}`),
}
