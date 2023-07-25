plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "io.github.opletter.chessground"
version = "1.0-SNAPSHOT"

kotlin {
    js(IR) {
        browser()
    }

    @Suppress("UNUSED_VARIABLE") // Suppress spurious warnings about sourceset variables not being used
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlin.js.wrappers)
                implementation(npm("chessground", "8.3.12"))
            }
        }
    }
}