export enum NotificationType {
  StageUp,
  WaitingForStageUp,
  Invite,
  Message,
}

export interface Notification {
  type: NotificationType
  message: string
  projectName: string
  projectId: number
  fromUser: number | null
}
