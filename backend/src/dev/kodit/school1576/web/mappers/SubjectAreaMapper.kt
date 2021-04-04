package dev.kodit.school1576.web.mappers

import dev.kodit.school1576.web.models.project.SubjectArea
import dev.kodit.school1576.web.models.project.SubjectAreaDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface SubjectAreaMapper {
    @JvmDefault
    fun subjectAreaToLong(subjectArea: SubjectArea) = subjectArea.id
    @Mapping(target = "title", source = "name")
    fun toSubjectAreaDto(subjectArea: SubjectArea): SubjectAreaDto
}
