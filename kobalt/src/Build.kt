import com.beust.kobalt.*
import com.beust.kobalt.plugin.packaging.*
import com.beust.kobalt.plugin.application.*
import com.beust.kobalt.plugin.kotlin.*

val bs = buildScript {
    repos()
}

val javaVersion = "1.8"
val kotlinVersion = "1.1.0"
val projectBuildSourceEncoding = "UTF-8"

val p = project {

    name = "telegramBotApi"
    group = "me.reckter"
    artifactId = name
    version = "0.16.9"

    sourceDirectories {
        path("src/main/java")
    }

    sourceDirectoriesTest {
        path("src/test/kotlin")
    }

    dependencies {
//        compile("com.beust:jcommander:1.48")
        compile("com.fasterxml.jackson.core:jackson-databind:2.8.0")
        compile("com.squareup.retrofit2:converter-jackson:2.1.0")
        compile("com.squareup.retrofit2:retrofit:2.1.0")
        compile("org.apache.commons:commons-io:1.3.2")
        compile("org.apache.httpcomponents:httpclient:4.3.4")
        compile("org.javassist:javassist:3.18.2-GA")
        compile("org.jetbrains.kotlin:kotlin-stdlib:1.0.3")
        compile("org.reflections:reflections:0.9.5")
    }

    dependenciesTest {
        compile("org.testng:testng:6.10")
        compile("org.jetbrains.kotlin:kotlin-test:1.0.3")

    }

    assemble {
        jar {
        }
    }

    application {
        mainClass = "com.example.MainKt"
    }


}
