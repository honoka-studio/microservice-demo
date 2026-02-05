import de.honoka.gradle.util.data.classifyProjects
import de.honoka.gradle.util.dsl.*

plugins {
    java
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.honoka.basic)
}

group = "de.honoka.demo.microservice"
version = libs.versions.p.root.get()

val projects = classifyProjects {
    jvm = subprojects - projects("web", rootPrefix = true)
    library = projects("common", rootPrefix = true)
    app = jvm - library
    businessApp = app - projects("gateway", rootPrefix = true)
}

projects.jvm {
    applier {
        java
        kotlin
        `kotlin-kapt`
        `kotlin-spring`
        `honoka-basic`
    }

    group = rootProject.group
    version = rootProject.version

    honoka.basic {
        configs {
            java(17)
            javaTask()
            kotlin()
            kapt()
        }

        dependencies {
            kotlin()
            springBootBom()
            springBootConfigProcessor()
        }
    }

    dependencies {
        implementation(platform(libs.spring.cloud.bom))
        implementation(platform(libs.spring.cloud.alibaba.bom))
    }
}

//服务项目公共配置
projects.app {
    applier {
        `spring-boot`
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
        implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation(libs.redisson.spring.boot.starter)
        implementation(libs.honoka.spring.boot.starter)
        implementation(project("common", true))
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}

//业务服务项目配置
projects.businessApp {
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
        runtimeOnly("com.mysql:mysql-connector-j")
        implementation("org.flywaydb:flyway-mysql")
        implementation(libs.mybatis.plus.spring.boot.starter)
        implementation(libs.mybatis.plus.jsqlparser)
    }
}

libs.versions.d.kotlin.coroutines
