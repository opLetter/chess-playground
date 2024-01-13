plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "io.github.opletter.chessground"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            api(libs.kotlin.js.wrappers) // exposes types like JsMap
            implementation(npm("chessground", "8.3.12"))
        }
    }
}