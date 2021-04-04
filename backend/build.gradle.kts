@file:Suppress("PropertyName")

import org.jetbrains.kotlin.utils.addToStdlib.indexOfOrNull
import java.util.*

val ktorVersion: String by project
val kotlinVersion: String by project
val coroutinesVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val hikariCpVersion: String by project
val postgresqlVersion: String by project
val junitVersion: String by project
val ktorOpenApiVersion: String by project
val krushVersion: String by project
val jetbrainsAnnotationsVersion: String by project
val yargVersion: String by project
val flywayVersion: String by project
val jacksonModuleVersion: String by project
val documentsVersion: String by project
val xDocReportVersion: String by project
val jsonapiVersion: String by project
val ktorAuthVersion: String by project
val apacheTikaVersion: String by project
val mapstructVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.johnrengelman.shadow")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "dev.kodit.school1576"
println("$group: $version")

val isCI: Boolean = System.getenv("CI")?.toBoolean() ?: false

if (isCI) {
    println("running in CI mode")
}

val javaOpts = listOf("-Xmx512m", "-Dfile.encoding=utf-8", "-Djava.net.preferIPv4Stack=true")

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
    applicationDefaultJvmArgs = javaOpts
}

kotlin.sourceSets["main"].kotlin.setSrcDirs(listOf("src"))
kotlin.sourceSets["test"].kotlin.setSrcDirs(listOf("test"))

sourceSets["main"].resources.setSrcDirs(listOf("resources"))
sourceSets["test"].resources.setSrcDirs(listOf("test_resources"))

ktlint {
    // version.set("0.38.0")
    verbose.set(true)
    ignoreFailures.set(true)
    // enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("com.zaxxer:HikariCP:$hikariCpVersion")

    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")

    implementation("ru.kod_it.lib.krush:runtime:$krushVersion")
    implementation("ru.kod_it.lib.krush:annotation-processor:$krushVersion")
    kapt("ru.kod_it.lib.krush:annotation-processor:$krushVersion")

    implementation("ru.kod_it.lib:jackson:$jacksonModuleVersion")
    implementation("ru.kod_it.lib:jsonapi:$jsonapiVersion")
    implementation("ru.kod_it.lib:ktor-auth:$ktorAuthVersion")

    implementation("ru.kod_it.lib:documents:$documentsVersion")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.document.docx:$xDocReportVersion")

    implementation("org.flywaydb:flyway-core:$flywayVersion")

    implementation("org.apache.tika:tika-core:$apacheTikaVersion")

    implementation("commons-io:commons-io:2.8.0")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

fun loadDotEnv(context: ProcessForkOptions, fileName: String) {
    val dotenv = file(fileName)
    if (dotenv.exists()) {
        dotenv.readLines().forEach {
            if (it.isEmpty())
                return@forEach
            val index = it.indexOfOrNull('=') ?: return@forEach
            val key = it.substring(0, index)
            val value = it.substring(index + 1)
            context.environment(key, value)
        }
    }
}

val compilerArgs = arrayOf(
    "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
    "-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI",
    "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI",
    "-Xjvm-default=enable"
)

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.freeCompilerArgs += compilerArgs

        if (!isCI) {
            dependsOn(ktlintFormat)
        }
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.freeCompilerArgs += compilerArgs
    }

    jar {
        manifest {
            attributes("Main-Class" to application.mainClass)
        }
    }

    shadowJar {
        isZip64 = true
        archiveFileName.set("${project.name}.jar")
//        minimize()
    }

    create("watch") {
        dependsOn(classes)
    }

    withType<JavaExec> {
        loadDotEnv(this, ".env")
        jvmArgs = javaOpts
    }
    withType<Test> {
        loadDotEnv(this, ".env.test")
        jvmArgs = javaOpts
    }

    val setVersion by registering {
        dependsOn(processResources)
        doFirst {
//            println("projectVersion $version")
            val file = File("$buildDir/resources/main/application.properties")
            val properties = Properties()
            properties["version"] = version
            properties.store(file.outputStream(), null)
        }
    }

    processResources {
        finalizedBy(setVersion)
    }
}
