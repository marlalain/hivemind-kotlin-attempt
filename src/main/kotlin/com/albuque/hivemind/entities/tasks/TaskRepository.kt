package com.albuque.hivemind.entities.tasks

import com.albuque.hivemind.exceptions.ServerNotReadyException
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KafkaStreams.State.RUNNING
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class TaskRepository(@Qualifier("kafkaStreams") private val kafkaStreams: KafkaStreams) {
	companion object {
		val logger: Logger = getLogger(TaskRepository::class.java)
		const val TOPIC_NAME = "tasks-topic"
		const val STREAM_NAME = "tasks-streams"
		const val TABLE_NAME = "tasks-table"
	}

	val store: ReadOnlyKeyValueStore<String, Task>
		get() {
			if (kafkaStreams.state() === RUNNING) {
				return kafkaStreams.store(StoreQueryParameters.fromNameAndType(TABLE_NAME, QueryableStoreTypes.keyValueStore()))
			}

			throw ServerNotReadyException()
		}

	fun findAll(): List<Task> {
		val tasks = mutableListOf<Task>()
		store.all().forEachRemaining { task -> tasks.add(task.value.copy(id = task.key)) }
		return tasks
	}

	fun findById(id: String): Task {
		return store.get(id)
	}

	fun has(id: String): Boolean = store.get(id) != null
}