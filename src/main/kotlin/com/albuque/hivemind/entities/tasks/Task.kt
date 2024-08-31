package com.albuque.hivemind.entities.tasks

import java.util.*

data class Task(
	val id: UUID? = UUID.randomUUID(),
	val title: String,
	val description: String?,
	var completed: Boolean = false
)
