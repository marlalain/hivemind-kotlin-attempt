package com.albuque.hivemind.entities.tasks

import com.albuque.hivemind.config.KafkaStreamConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskProducer {
	private val logger: Logger = getLogger(TaskProducer::class.java)
	private val producer: KafkaProducer<String, Task> = KafkaProducer(KafkaStreamConfig.props)

	fun send(task: Task): Task {
		logger.info("Sending task $task")
		val id = task.id ?: UUID.randomUUID().toString()
		val newTask = task.copy(id = id)
		producer.send(ProducerRecord(TaskRepository.TOPIC_NAME, newTask.id, newTask))

		return newTask
	}

	fun delete(taskId: String) {
		producer.send(ProducerRecord(TaskRepository.TOPIC_NAME, taskId, null))
	}
}