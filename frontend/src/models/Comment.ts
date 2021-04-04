export interface CommentDto {
  author: string
  authorId: number
  isSelfComment: boolean
  dateTime: string
  text: string
  files?: File[] | string[] | null
}
