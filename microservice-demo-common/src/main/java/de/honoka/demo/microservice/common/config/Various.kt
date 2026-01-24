package de.honoka.demo.microservice.common.config

import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients(basePackages = ["de.honoka.demo.microservice"])
class FeignBaseConfig
