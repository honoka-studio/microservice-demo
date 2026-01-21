package de.honoka.demo.microservice.service1.controller

import cn.hutool.core.lang.UUID
import de.honoka.sdk.spring.starter.redis.basic.DefaultRedisTemplate
import de.honoka.sdk.util.kotlin.lang.log
import de.honoka.sdk.util.kotlin.lang.tryLock
import de.honoka.sdk.util.web.ApiResponse
import org.redisson.api.RedissonClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit
import kotlin.concurrent.withLock

@RequestMapping("/redis")
@RestController
class RedisController(
    private val redisTemplate: DefaultRedisTemplate,
    private val redissonClient: RedissonClient
) {

    private val prefix = "service1:redis"

    private val lock1 = redissonClient.getLock("$prefix:lock1")

    @GetMapping("/test1")
    fun test1(): ApiResponse<*> {
        val id = UUID.randomUUID().toString()
        val value = UUID.randomUUID().toString()
        val key = "$prefix:test1:$id"
        redisTemplate.opsForValue().set(
            "$key:raw", value, 60, TimeUnit.SECONDS
        )
        val res = ApiResponse.success(id)
        redisTemplate.opsForValue().set(
            "$key:res", res, 60, TimeUnit.SECONDS
        )
        log.info("\nvalue: $value")
        log.info("\nvalue from redis: ${redisTemplate.opsForValue()["$key:raw"]}")
        log.info("\n${redisTemplate.opsForValue()["$key:res"]}")
        return res
    }

    @GetMapping("/test2")
    fun test2(): ApiResponse<*> {
        lock1.withLock {
            TimeUnit.SECONDS.sleep(20)
        }
        return ApiResponse.success()
    }

    @GetMapping("/test3")
    fun test3(): ApiResponse<*> {
        lock1.tryLock(5) {
            TimeUnit.SECONDS.sleep(5)
            return ApiResponse.success()
        }.getOrElse {
            return ApiResponse.fail(null)
        }
    }
}
