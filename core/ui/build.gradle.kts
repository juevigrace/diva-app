plugins {
    id("divabuild.library-ui")
}

compose.resources {
    generateResClass = always
    publicResClass = true
    packageOfResClass = "com.diva.core.ui.resources"
}
