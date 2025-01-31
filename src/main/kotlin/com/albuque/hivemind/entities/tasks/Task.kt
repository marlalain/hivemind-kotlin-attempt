package com.albuque.hivemind.entities.tasks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize

typealias TaskId = String

@JsonSerialize
data class Task @JsonCreator constructor(
	@JsonProperty("id")
	val id: TaskId? = null,
	@JsonProperty("title")
	val title: String,
	@JsonProperty("description")
	val description: String?,
	@JsonProperty("completed")
	var completed: Boolean = false
)
