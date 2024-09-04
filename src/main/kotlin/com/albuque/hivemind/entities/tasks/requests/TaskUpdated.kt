package com.albuque.hivemind.entities.tasks.requests

import com.albuque.hivemind.entities.tasks.Task
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
data class TaskUpdated(
	val title: String?,
	val description: String?,
	val completed: Boolean?,
) {
	fun toTask(task: Task) = Task(
		id = task.id,
		title = title ?: task.title,
		description = description ?: task.description,
		completed = completed ?: task.completed,
	)
}
