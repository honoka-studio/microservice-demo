package de.honoka.demo.microservice.common.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@EnableFeignClients(basePackages = ["de.honoka.demo.microservice.common"])
@ComponentScan("de.honoka.demo.microservice.common")
class MainBaseConfig
