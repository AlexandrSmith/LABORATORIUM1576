package dev.kodit.school1576.web.mappers

import org.mapstruct.Mapper

@Mapper
interface ChatMapper {
    //
    // @Mappings(
    //     Mapping(target = "user", source = "user"),
    //     Mapping(target = "project", source = "project"),
    // )
    // fun toNotificationDto(notification: Notification): NotificationDto
    //
    // @JvmDefault
    // fun userToId(notification: Notification): Long = notification.user.id
    //
    // @JvmDefault
    // fun projectToId(notification: Notification): Long = notification.project.id
    // @Mappings(
    //     Mapping(target = "user", source = "user"),
    //     Mapping(target = "project", source = "project"),
    // )
    // fun toMessageDto(message: Message): MessageDto

    // @JvmDefault
    // fun userToId(user: User): Long = user.id
    //
    // @JvmDefault
    // fun projectToId(project: Project): Long = project.id
}
