package com.albuque.hivemind

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HivemindApplication

fun main(args: Array<String>) {
	runApplication<HivemindApplication>(*args)
}
