package dev.kodit.school1576

import dev.kodit.school1576.db.DatabaseFactory
import io.ktor.locations.KtorExperimentalLocationsAPI
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import ru.kod_it.lib.ktor_auth.AuthService
import java.time.LocalDate

data class TestData(
    val name: String,
    val createdAt: LocalDate
)

@KtorExperimentalLocationsAPI
class ApplicationTest {
    // @Test
    // fun testRoot() {
    //     withTestApplication({ module() }) {
    //         handleRequest(HttpMethod.Get, "/").apply {
    //             assertEquals(HttpStatusCode.OK, response.status())
    //             assertEquals("HELLO WORLD!", response.content)
    //         }
    //     }
    // }

    @Test
    fun initDB() {
        DatabaseFactory.init()
        transaction {
            // SchemaUtils.createMissingTablesAndColumns(ClientTable, EmployeeTable)
        }
    }

    @Test
    fun password() {
        println(AuthService.getHash("12345678"))
    }

    @Test
    fun test() {
        // println("data:,Hello%2C%20World!".substringBefore(",").substringBefore(";"))
        // println("data:text/plain;base64,SGVsbG8sIFdvcmxkIQ==".substringBefore(",").substringBefore(";"))
        //
        // println(ContentType.parse("").match(ContentType.Image.Any))
        // println(ContentType.parse("text/plain").match(ContentType.Image.Any))
        // println(ContentType.parse("image/jpeg").match(ContentType.Image.Any))

        // println(FileStorageService.handleUrl("/files/test.jpg"))

        // val sp = SpecialOffer(
        //     id = 0,
        //     name = "Все по 10%",
        //     value = 10.0,
        //     condition = SpecialOfferType.Discount,
        //     offerBegin = LocalDate.of(2020, 10, 1),
        //     offerEnd = LocalDate.of(2020, 11, 1),
        //     label = "",
        //     description = ""
        // )
        //
        // (1..10).forEach {
        //     val elapsed = measureNanoTime {
        //         val dto = sp.toSpecialOfferNameDto()
        //     }
        //     val elapsed2 = measureNanoTime {
        //         val dto = SpecialOfferNameDto(sp.id, sp.name)
        //     }
        //     println("========== $it")
        //     println("elapsed $elapsed")
        //     println("elapsed2 $elapsed2")
        //     println("==========")
        // }
    }
}
