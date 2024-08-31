package com.albuque.hivemind.entities.tasks

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class TaskProducer(private val kafka: KafkaTemplate<String, Task>) {
	fun send(topic: String, task: Task) {
		kafka.send(topic, task)
	}
}