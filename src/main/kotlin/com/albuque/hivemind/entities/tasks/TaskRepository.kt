package com.albuque.hivemind.entities.tasks

import com.albuque.hivemind.config.KafkaStreamConfig.Companion.props
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class TaskRepository {
	companion object {
		val logger: Logger = getLogger(TaskRepository::class.java)
		const val TOPIC_NAME = "tasks-topic"
		const val STREAM_NAME = "tasks-streams"
	}

	private val tasks = mutableMapOf<String, Task>()

	@Bean
	fun kafkaConsumer(): KafkaConsumer<String, Task> {
		val kafkaConsumer: KafkaConsumer<String, Task> = KafkaConsumer(
			props.plus(
				mapOf(
					ConsumerConfig.GROUP_ID_CONFIG to "hivemind-group",
					ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
				)
			)
		)
		kafkaConsumer.subscribe(listOf(TOPIC_NAME))

		thread {
			while (true) {
				val message = kafkaConsumer.poll(java.time.Duration.ofMinutes(1))
				message.forEach { record ->
					logger.info("Consuming ${record.key()} -> ${record.value()}")
					tasks[record.key()] = record.value()
				}
			}
		}

		return kafkaConsumer.also { Runtime.getRuntime().addShutdownHook(Thread(it::close)) }
	}

	fun findAll(): List<Task> = tasks.map { it.value.copy(id = it.key) }
}