import com.varabyte.kobweb.gradle.application.extensions.AppBlock
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.opletter.chesspg"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        legacyRouteRedirectStrategy = AppBlock.LegacyRouteRedirectStrategy.DISALLOW
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
            implementation(compose.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.chess)
        }
        jsMain.dependencies {
            implementation(compose.html.core)
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