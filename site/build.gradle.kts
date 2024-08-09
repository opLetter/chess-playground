import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.opletter.chesspg"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description = "Powered by Kobweb"
        }
//        server.remoteDebugging.enabled = true
    }
}

kotlin {
    configAsKobwebApplication("chesspg", includeServer = true)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.chess)
        }
        jsMain.dependencies {
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(projects.chessground)
        }
        jvmMain.dependencies {
            implementation(libs.kobweb.api)
        }
    }
}