package de.honoka.demo.microservice.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoGatewayApp

fun main(args: Array<String>) {
    runApplication<DemoGatewayApp>(*args)
}
