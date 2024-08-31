package com.albuque.hivemind.entities.tasks

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class TaskConsumer {
	companion object {
		val logger: Logger = Logger.getLogger(TaskConsumer::class.qualifiedName)
	}

	@KafkaListener(topics = ["tasks"], groupId = "my-group")
	fun consume(task: Task) {
		logger.info("Received task $task")
	}
}