rootProject.name = "spring-webflux-response-wrapper"
include("lib")

pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}