buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.0")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    kotlin("kapt") version "2.0.21"
}
