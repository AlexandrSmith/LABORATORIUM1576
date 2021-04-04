import { QInput } from 'quasar'

export interface Project {
  id?: number
  name: string
  status: ProjectStatus
  startDate: string
}

export enum ProjectStatus {
  Waiting = 'Waiting',
  InWork = 'InWork',
  Done = 'Done',
}

export interface ProjectInfoDto {
  id: number
  name: string
  annotation: string
  mark: number
  subjectAreas: SubjectAreaDto[]
  experts: MemberDto[]
  stage: number
  waitingForStageUp: boolean
  createDate: string
  learner: UserShortTableDto
}

export interface FilterObject {
  name?: string
  sortOrder: SortOrderType
}

enum SortOrderType {
  ByName = 'ByName',
  ByDate = 'ByDate',
  BySubjects = 'BySubjects',
  ByLearner = 'ByLearner',
  ByTeacher = 'ByTeacher',
}

export interface UserShortTableDto {
  id?: number | undefined
  fullName?: string | undefined
}

export interface ProjectDto {
  id?: number | null
  locked: boolean
  learner: UserShortTableDto
  name: string
  annotation: string
  createDate: string
  subjectAreas: SubjectAreaDto[]
  members: MemberDto[]
  researchTopic: string
  researchObject: string
  researchSubject: string
  researchPurpose: string
  researchHypothesis: string
  researchMethods: string
  relevance: string
  explanatory: string
  problems: string
  product: string
  tasks: TaskDto[]
  workResult: string
  conclusion: string
  resultFiles: FileInfo[]
  deadline?: string
  shared?: boolean
  permissionToPublish?: boolean
  mark: number
  stage: number
  waitingForStageUp: boolean
}

export interface TaskDto {
  id?: number
  title: string
  projectId: number
  isDone: boolean
  result: string
  files: FileInfo[]
}

export interface SubjectAreaDto {
  id?: number
  title: string
  description: string
}

export interface MemberDto {
  id: number
  type: MemberType
  status: MemberStatus
  userId: number
  fullName: string
}

export interface InfoBlockRef extends Vue {
  focusInput: QInput
}

export enum MemberType {
  Teacher = 'Teacher',
  Expert = 'Expert',
}

export enum MemberStatus {
  Invited = 'Invited',
  Accepted = 'Accepted',
}

export interface FileInfo {
  projectId?: number
  taskId?: number
  fileId?: number
  filename: string
  url: string
  pinned?: boolean
}

export interface FileRef extends FileInfo {
  uploadName: string
}

export interface FileDto {
  projectId: number
  taskId?: number
  file: string | File | undefined
}

export interface OptionValue {
  label: string
  value: number | string
}

export interface MessageDto {
  id?: number
  userId: number
  fullName: string
  dateTime: string
  text: string
}

export interface NewMessageDto {
  dateTime: string
  text: string
}
