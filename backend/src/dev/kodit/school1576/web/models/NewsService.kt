package dev.kodit.school1576.web.models

import dev.kodit.school1576.web.models.project.NewsDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object NewsService {

    private fun UpdateBuilder<Any>.from(news: NewsDto, image: Long?) {
        this[NewsTable.author] = news.author
        this[NewsTable.dateTime] = LocalDateTime.parse(news.dateTime.subSequence(0, 19))
        this[NewsTable.title] = news.title
        this[NewsTable.text] = news.text
        this[NewsTable.image] = image
    }

    fun list() = transaction {
        NewsTable.leftJoin(FileRefTable).selectAll().orderBy(NewsTable.dateTime).toNewsList()
    }

    fun create(news: NewsDto) {
        transaction {
            val imageId = if (news.image != null) FileStorageService.saveInputStream(news.image) else null
            NewsTable.insert { it.from(news, imageId) }
        }
    }

    fun update(id: Long, news: NewsDto) {
        transaction {
            val imageId = if (news.image != null) FileStorageService.saveInputStream(news.image)
            else NewsTable.select { NewsTable.id eq id }.single()[NewsTable.image]
            NewsTable.update({ NewsTable.id eq id }) { it.from(news, imageId) }
        }
    }

    fun delete(id: Long) { transaction { NewsTable.deleteWhere { NewsTable.id eq id } } }

    fun get(id: Long) = transaction {
        NewsTable.leftJoin(FileRefTable).select { NewsTable.id eq id }.orderBy(NewsTable.dateTime).singleOrNull()?.toNews()
    }
}
