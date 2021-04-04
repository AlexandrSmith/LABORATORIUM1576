package dev.kodit.school1576.db

import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import ru.kod_it.lib.krush.JsonColumnType
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class JsonAgg<T : Any>(private val table: Table, type: KType) :
    Function<T>(JsonColumnType<T>(type, false)) {

    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        if (table.autoIncColumn == null)
            error("Не поддерживаются таблицы автоинреметного столбца")
        // COALESCE(json_agg(E) FILTER (WHERE E.id IS NOT NULL), '[]')
        append(
            "COALESCE(json_agg(DISTINCT ${table.tableName}) FILTER (WHERE ${table.tableName}.${table.autoIncColumn!!.name} IS NOT NULL), '[]')"
        )
    }
}

inline fun <reified T : Any> jsonAgg(table: Table): JsonAgg<List<T>> {
    return JsonAgg(table, typeOf<List<T>>())
}
