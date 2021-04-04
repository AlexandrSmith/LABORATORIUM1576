import createApiClient, { ExportFile, handleDownload } from 'src/api-client'
import { crudFactory } from 'src/api/base'
import { FileDto, MemberType, ProjectDto, ProjectInfoDto, SubjectAreaDto } from 'src/models/Project'
import { UserDto } from 'src/models/User'

const client = createApiClient('/projects')
const subjectAreas = createApiClient('/projects/subject-areas')

export default {
  ...crudFactory<ProjectInfoDto, ProjectDto>(client),
  filteredList: (filter: URLSearchParams) => client.get<ProjectInfoDto[]>(`main?${filter.toString()}`),
  adminFilteredList: (filter: URLSearchParams) => client.get<ProjectDto[]>(`admin?${filter.toString()}`),
  getMainProject: (id: number) => client.get<ProjectDto>(`main/${id}`),
  subjects: crudFactory<SubjectAreaDto, SubjectAreaDto>(subjectAreas),
  teachersList: (subjectIds: number[]) => client.get<UserDto[]>('teachers', { subjectIds }),
  expertsList: (subjectIds: number[]) => client.get<UserDto[]>('experts', { subjectIds }),
  invite: (id: number, userIds: number[], type: MemberType) =>
    client.post(`${id}/member/invite`, null, { userIds, type }),
  acceptMentoring: (id: number, statement: boolean) => client.post(`${id}/member/accept`, statement),
  acceptStage: (id: number, stage: number) => client.post(`${id}/stage-up`, stage),
  declineStage: (id: number) => client.post(`${id}/decline`),
  waitingForStageUp: (id: number, stage: number) => client.post(`${id}/waiting-for-stage-up`, stage),
  updateMark: (id: number, mark: number) => client.patch(`${id}/update-mark`, mark),
  addFile: (id: number, file: Partial<FileDto>) => client.postMultiPart(`${id}/files`, file),
  deleteFile: (id: number, fileId: number) => client.delete(`${id}/files/${fileId}`),
  lockOrUnlock: (id: number, isLock: boolean) => client.patch(`${id}/lock-or-unlock`, isLock),
  generateReport: (id: number) => client.get<ExportFile>(`${id}/report`).then(handleDownload),
  setDeadline: (id: number, deadline: string) => client.patch(`${id}/set-deadline`, deadline),
  share: (id: number, status: boolean) => client.patch(`${id}/share`, status),
  sharePermission: (id: number, status: boolean) => client.patch(`${id}/permission_to_publish`, status),
  pinFile: (projectId: number, fileId: number, pinned: boolean) => client.patch(`${projectId}/files/${fileId}`, pinned),
}
