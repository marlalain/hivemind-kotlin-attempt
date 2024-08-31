package com.albuque.hivemind.entities.tasks

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController(private val producer: TaskProducer) {
	@PostMapping
	fun publish(@RequestBody task: Task) {
		producer.send("tasks", task)
		ResponseEntity.created(URI.create("/api/v1/tasks/${task.id}"))
	}
}