pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Vinilos"
includeBuild("libraries/androidx-compose-material3-pullrefresh/library") {
    dependencySubstitution {
        substitute(module("me.omico.compose:compose-material3-pullrefresh")).using(project(":"))
    }
}
include(":app")