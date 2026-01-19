package de.honoka.demo.microservice.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoUserApp

fun main(args: Array<String>) {
    runApplication<DemoUserApp>(*args)
}
