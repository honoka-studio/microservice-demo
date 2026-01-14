package de.honoka.demo.microservice.service1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoService1App

fun main(args: Array<String>) {
    runApplication<DemoService1App>(*args)
}
