@file:Suppress("UnstableApiUsage")

pluginManagement {
    val customRepositories: RepositoryHandler.() -> Unit = {
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        gradlePluginPortal()
        mavenLocal()
        maven("https://mirrors.honoka.de/maven-repo/release")
        maven("https://mirrors.honoka.de/maven-repo/development")
    }
    repositories(customRepositories)
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories(customRepositories)
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "microservice-demo"

include("${rootProject.name}-common")
include("${rootProject.name}-gateway")
include("${rootProject.name}-auth")
include("${rootProject.name}-user")
include("${rootProject.name}-service1")
include("${rootProject.name}-web")
