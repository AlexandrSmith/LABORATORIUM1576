import { FileInfo, FileRef } from 'src/models/Project'

export interface NewsDto {
  id?: number
  title: string
  dateTime: string
  text: string
  image: FileRef | string | File | undefined
  author: string
}
