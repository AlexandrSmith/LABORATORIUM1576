plugins {
    id("pl.allegro.tech.build.axion-release") version "1.12.0"
}

scmVersion {
    tag.prefix = "v"
    tag.versionSeparator = ""
    useHighestVersion = true
    repository.pushTagsOnly = true
    branchVersionIncrementer = mapOf(
        "feature/.*" to "incrementMinor"
    )
}

val jarLibDir = project.file("lib")

allprojects {
    version = "1.0.0"

    repositories {
        exclusiveContent {
            forRepository {
                maven {
                    url = jarLibDir.toURI()
                }
            }
            filter {
                includeGroupByRegex("dev\\.kodit.*")
                includeGroupByRegex("ru\\.kod_it.*")
            }
        }
        // maven {
        //     url = uri("https://gitlab.abago.net/api/v4/groups/23/-/packages/maven")
        //     name = "KodIT maven repo"
        //
        //     val gitLabPrivateToken: String by project
        //     val jobToken = System.getenv("CI_JOB_TOKEN")
        //
        //     if (jobToken.isNullOrEmpty()) {
        //         credentials(HttpHeaderCredentials::class) {
        //             name = "Private-Token"
        //             value = gitLabPrivateToken
        //         }
        //     } else {
        //         credentials(HttpHeaderCredentials::class) {
        //             name = "Job-Token"
        //             value = jobToken
        //         }
        //     }
        //     authentication {
        //         create<HttpHeaderAuthentication>("header")
        //     }
        // }
        maven { setUrl("https://nexus.xanter.dev/repository/maven-public") }
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
