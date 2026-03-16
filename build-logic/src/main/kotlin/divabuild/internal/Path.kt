package divabuild.internal

import org.gradle.api.Project

fun Project.cleanPath(): String {
    val pathSegments = path.split(":").filter { it.isNotEmpty() }

    // Remove "features" from the path if it exists
    val filteredSegments = if (pathSegments.firstOrNull() == "features") {
        pathSegments.drop(1)
    } else {
        pathSegments
    }

    // Split all segments by hyphens and flatten
    val allParts = filteredSegments.flatMap { it.split("-") }

    // Remove ALL duplicates (not just consecutive) while preserving order
    val dedupedParts = allParts.distinct()

    val newPath = dedupedParts.joinToString(".")
    val cleanPath = newPath.let { path ->
        val mutList = path.split(".").toMutableList()
        if (mutList.contains("api") && mutList.contains("client")) {
            return@let mutList.joinToString(".")
        }
        if (mutList.last() == "client" || mutList.last() == "server") {
            mutList.removeAt(mutList.lastIndex)
        }
        mutList.joinToString(".")
    }
    return cleanPath
}
