package com.albuque.hivemind.entities.tasks

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController(
	private val producer: TaskProducer,
	private val repository: TaskRepository
) {
	companion object {
		private val logger: Logger = getLogger(TaskController::class.java)
	}

	@PostMapping
	fun create(@RequestBody task: Task): ResponseEntity<Task> {
		logger.info("Creating task $task")
		return ok(producer.send(task))
	}

	@GetMapping
	fun all(): ResponseEntity<List<Task>> = ok(repository.findAll())
}