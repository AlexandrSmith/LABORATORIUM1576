import createApiClient from 'src/api-client'
import { crudFactory } from 'src/api/base'
import { Role, UserDto, UserInfo } from 'src/models/User'

const client = createApiClient('/users')

export default {
  ...crudFactory<UserDto, UserDto>(client),

  info: () => client.get<UserInfo>('info'),

  roles: () => client.get<Role[]>('roles'),
}
