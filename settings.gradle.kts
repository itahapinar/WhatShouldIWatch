pluginManagement {
    repositories {
        google {
            content {includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Corrected syntax for creating a Properties object
val localProperties = java.util.Properties()
// Use rootProject.file for better path handling
val localPropertiesFile = file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { input ->
        localProperties.load(input)
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ( url = "https://jitpack.io" ) // ✅ JitPack bağlantısı
    }
}




rootProject.name = "neizlesem"
include(":app")
