package de.honoka.demo.microservice.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoAuthApp

fun main(args: Array<String>) {
    runApplication<DemoAuthApp>(*args)
}
