import { UserDto } from 'src/models/User'

export function fullName(user: UserDto) {
  if (user.surname || user.name || user.patronymic)
    return `${user.surname || ''} ${user.name || ''} ${user.patronymic || ''}`
  else return null
}
