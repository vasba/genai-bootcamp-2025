import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.0"
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
}

kotlin {
    // Target configuration
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    port = 8090
                    static = (static ?: mutableListOf()).apply {
                        add(project.rootDir.path)
                        add(project.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    // Source sets configuration
    sourceSets {
        // Common code that could be shared with future platforms
        commonMain {
            dependencies {
                // Compose dependencies
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                
                // Core libraries
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                
                // ViewModels and Lifecycle
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                
                // Common Ktor dependencies
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        // Web/Wasm specific code
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        // Tests configuration
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

// Development configurations
compose {
    experimental {
        web.application {}
    }
}
