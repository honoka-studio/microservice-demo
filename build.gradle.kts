import de.honoka.gradle.util.dsl.libs
import de.honoka.gradle.util.dsl.projects
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

plugins {
    java
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.honoka.basic)
}

allprojects {
    group = "de.honoka.demo.microservice"
    version = libs.versions.p.root.get()
}

//非服务项目
val notServiceProjects = projects()

//非业务服务项目
val notBusinessServiceProjects = projects("microservice-demo-gateway")

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "de.honoka.gradle.plugin.basic")

    if(project !in notServiceProjects) {
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "org.springframework.boot")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
    }

    honoka.basic {
        dependencies {
            kotlin()
            springBootBom()
            springBootConfigProcessor()
        }
    }
    
    dependencies {
        implementation(platform(libs.spring.cloud.bom))
        implementation(platform(libs.spring.cloud.alibaba.bom))
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
        implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
        implementation(libs.honoka.spring.boot.starter)
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    //业务服务应用配置
    dependencies {
        if(project in notBusinessServiceProjects) return@dependencies
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
        runtimeOnly("com.mysql:mysql-connector-j")
        implementation("org.flywaydb:flyway-mysql")
        implementation(libs.mybatis.plus.spring.boot.starter)
        implementation(libs.mybatis.plus.jsqlparser)
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation(libs.redisson.spring.boot.starter)
    }
    
    tasks {
        withType<JavaCompile> {
            options.run {
                encoding = StandardCharsets.UTF_8.name()
                val compilerArgs = compilerArgs as MutableCollection<String>
                compilerArgs += listOf("-parameters")
            }
        }

        /*
         * 由于除了原本的compileKotlin任务外，还存在compileTestKotlin和kapt的KaptGenerateStubsTask
         * （KotlinCompile的子类）任务需要配置，因此这里不能使用“compileKotlin {}”块。
         */
        withType<KotlinCompile> {
            compilerOptions {
                freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all")
            }
        }

        withType<Test> {
            useJUnitPlatform()
            workingDir = rootDir
        }
    }

    kapt {
        keepJavacAnnotationProcessors = true
    }
}

libs.versions.d.kotlin.coroutines
