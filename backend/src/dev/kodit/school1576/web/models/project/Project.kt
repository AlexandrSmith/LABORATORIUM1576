package dev.kodit.school1576.web.models.project

import dev.kodit.school1576.web.models.FileRef
import dev.kodit.school1576.web.models.users.User
import ru.kod_it.lib.krush.annotation.ManyToManyOptions
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "projects")
data class Project(
    @GeneratedValue @Id
    val id: Long,
    val name: String,
    @Column(length = 0)
    val annotation: String,
    @Column(name = "research_topic", length = 0)
    val researchTopic: String,
    @Column(name = "research_object", length = 0)
    val researchObject: String,
    @Column(name = "research_subject", length = 0)
    val researchSubject: String,
    @Column(name = "research_purpose", length = 0)
    val researchPurpose: String,
    @Column(name = "research_hypothesis", length = 0)
    val researchHypothesis: String,
    @Column(name = "research_methods", length = 0)
    val researchMethods: String,
    @Column(name = "work_result", length = 0)
    val workResult: String,
    @Column(name = "permission_to_publish", length = 0)
    val permissionToPublish: Boolean,

    val deadline: LocalDate?,

    val shared: Boolean?,

    @Column(name = "is_locked")
    val locked: Boolean,

    val relevance: String,
    val explanatory: String?,
    val problems: String?,
    val product: String?,

    @Column(length = 0)
    val conclusion: String,
    val mark: Int,
    val stage: Int,
    @Column(name = "waiting_for_stage_up")
    val waitingForStageUp: Boolean,

    @Column(name = "create_date")
    val createDate: LocalDate,

    @ManyToOne
    @JoinColumn(name = "learner_id")
    val learner: User,

    @ManyToMany
    @ManyToManyOptions("projects_subject_area", "project_id", "subject_area_id")
    val subjectAreas: List<SubjectArea>,

    @ManyToMany
    @ManyToManyOptions("projects_files", "project_id", "file_id")
    val files: List<FileRef>,
)

@Entity
@Table(name = "subject_area")
data class SubjectArea(
    @GeneratedValue @Id
    val id: Long,
    val name: String,
    val description: String,

    // @ManyToMany
    // @ManyToManyOptions("projects_subject_area", "subject_area_id", "project_id")
    // val projects: List<Project>,
    // @ManyToMany
    // @ManyToManyOptions("users_subject_areas", "subject_area_id", "user_id")
    // val users: List<User>
)

// @Entity
// @Table(name = "projects_subject_area")
// data class ProjectsSubjectArea(
//     @ManyToOne
//     @JoinColumn(name = "subject_area_id")
//     val subject: SubjectArea,
//
//     @ManyToOne
//     @JoinColumn(name = "projects_id")
//     val project: Project,
// )

@Entity
@Table(name = "tasks")
data class Task(
    @GeneratedValue @Id
    val id: Long,
    val title: String,

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    @Column(name = "is_done")
    val isDone: Boolean,
    @Column(length = 0)
    val result: String,

    @ManyToMany
    @ManyToManyOptions("task_files", "task_id", "file_id")
    val files: List<FileRef>,
)

@Entity
@Table(name = "members")
data class Member(
    @GeneratedValue @Id
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @JoinColumn(name = "projects_id")
    val project: Project,

    @Column(name = "member_type")
    @Enumerated(EnumType.STRING)
    val type: MemberType,

    @Enumerated(EnumType.STRING)
    val status: MemberStatus,
)
