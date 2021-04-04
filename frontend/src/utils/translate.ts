import { ProjectStatus } from 'src/models/Project'
import { Role } from 'src/models/User'

const dictionary: Record<string, string> = {
  [ProjectStatus.Waiting]: 'Ожидает',
  [ProjectStatus.InWork]: 'В работе',
  [ProjectStatus.Done]: 'Завершен',

  [Role.Teacher]: 'Учитель',
  [Role.Expert]: 'Эксперт',
  [Role.Admin]: 'Администратор',
  [Role.Student]: 'Ученик',
}

export function translate(word: string): string {
  return dictionary[word] ?? word
}
