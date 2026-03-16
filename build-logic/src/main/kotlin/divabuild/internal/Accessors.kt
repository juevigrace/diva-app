package divabuild.internal

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.kotlin(configure: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure("kotlin", configure)
}
