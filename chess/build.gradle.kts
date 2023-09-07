plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "io.github.opletter.chess"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser()
    }
    jvm()
}