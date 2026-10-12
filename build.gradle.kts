import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}

val jitpackGroup = System.getenv("GROUP") ?: "com.github.vtabk2"
val jitpackArtifact = System.getenv("ARTIFACT") ?: "CodeBaseV2"
val jitpackVersion = "1.0.0"

subprojects {
    val moduleGroup = if (jitpackGroup.endsWith(".$jitpackArtifact")) {
        jitpackGroup
    } else {
        "$jitpackGroup.$jitpackArtifact"
    }
    group = if (path == ":core") jitpackGroup else moduleGroup
    version = System.getenv("VERSION") ?: jitpackVersion

    pluginManager.withPlugin("com.android.library") {
        apply(plugin = "maven-publish")

        extensions.configure<LibraryExtension>("android") {
            publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }
        }

        afterEvaluate {
            val component = components.findByName("release") ?: return@afterEvaluate

            extensions.configure<PublishingExtension>("publishing") {
                publications {
                    register<MavenPublication>("release") {
                        from(component)
                        groupId = project.group.toString()
                        artifactId = if (path == ":core") jitpackArtifact else project.name
                        version = project.version.toString()
                    }
                }
            }
        }
    }
}
