import java.net.URI



pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://storage.googleapis.com/r8-releases/raw")
        }

        maven { url = URI("https://android-sdk.is.com/") }

        maven { url = URI("https://artifact.bytedance.com/repository/pangle/") }

        maven { url = URI("https://maven.aliyun.com/repository/jcenter") }

        maven { url = URI("https://jitpack.io") }

        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle/")
        }
    }
}


rootProject.name = "WodoX"


include(":app:intro")
include(":app:common")
include(":app:resources")
include(":app:home")
include(":app:setting")
include(":app:main")
include(":app")
include(":domain:asset")
include(":data:asset")
include(":data:prefs")
include(":data:remote")
include(":data:common")
include(":data:main")
include(":domain:chat")
include(":domain:main")
include(":domain:remote")
include(":domain:common")
include(":domain:home")
include(":data:home")
include(":config")
include(":domain:base")
include(":data:base")
include(":app:docs")
include(":rteditor")
include(":app:calendar")
include(":app:chat")
include(":app:mywork")
include(":data:docs")
include(":domain:docs")
include(":app:auth")
include(":domain:user")
include(":data:user")
include(":data:chat")
include(":app:core")


