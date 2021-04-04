export interface UserDto {
  id?: number
  username: string
  password?: string
  surname: string
  name: string
  patronymic?: string
  status: UserStatus
  role: Role
  areas: number[]
}

export interface UserTableDto {
  id?: number
  fullName: string
  username: string
  status: UserStatus
  role: string
  roleTitle: string
}

export enum UserStatus {
  Active = 'Active',
  Blocked = 'Blocked',
}

export interface UserInfo {
  id: number
  username: string
  fullName: string
  shortName: string
  role: Role
}

export enum Role {
  Admin = 'Admin',
  Teacher = 'Teacher',
  Student = 'Student',
  Expert = 'Expert',
}
