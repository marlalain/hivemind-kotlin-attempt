package com.albuque.hivemind.entities.tasks.requests

import com.albuque.hivemind.entities.tasks.Task
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*

@JsonSerialize
data class TaskCreated @JsonCreator constructor(
	val title: String,
	val description: String? = null,
) {
	fun toTask() = Task(
		id = UUID.randomUUID().toString(),
		title = title,
		description = description,
	)
}