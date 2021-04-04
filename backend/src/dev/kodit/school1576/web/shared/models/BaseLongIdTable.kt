package dev.kodit.school1576.web.shared.models

import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.toEntity
import ru.kod_it.lib.krush.dateTimeTz
import java.time.ZonedDateTime

abstract class BaseLongIdTable(name: String) : LongIdTable(name) {
    val createdAt = dateTimeTz("created").clientDefault { ZonedDateTime.now() }
    val updatedAt = dateTimeTz("updated").nullable()
}

abstract class BaseLongEntity(id: EntityID<Long>, table: BaseLongIdTable) : LongEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseLongEntityClass<E : BaseLongEntity>(table: BaseLongIdTable) : LongEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = ZonedDateTime.now()
                } catch (e: Exception) {
                    // nothing much to do here
                }
            }
        }
    }
}

val BaseLongEntity.idValue: Long
    get() = this.id.value
