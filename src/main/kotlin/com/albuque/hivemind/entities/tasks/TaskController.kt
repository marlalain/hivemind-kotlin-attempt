package com.albuque.hivemind.entities.tasks

import com.albuque.hivemind.entities.tasks.requests.TaskCreated
import com.albuque.hivemind.entities.tasks.requests.TaskUpdated
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.accepted
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController(
	private val producer: TaskProducer,
	private val repository: TaskRepository,
) {
	companion object {
		private val logger: Logger = getLogger(TaskController::class.java)
	}

	@PostMapping
	fun create(@RequestBody task: TaskCreated): ResponseEntity<Task> {
		val newTask = producer.send(task.toTask())
		logger.info("Created task $newTask")
		return created(URI("/api/v1/tasks/${newTask.id}")).build()
	}

	@GetMapping
	fun all(): ResponseEntity<List<Task>> = ok(repository.findAll())

	@GetMapping("/{taskId}")
	fun one(@PathVariable taskId: String): ResponseEntity<Task> = ok(repository.findById(taskId))

	@PatchMapping("/{taskId}")
	fun update(@PathVariable taskId: String, @RequestBody task: TaskUpdated): ResponseEntity<Task> {
		return if (repository.has(taskId)) {
			val oldTask = repository.findById(taskId)
			val newTask = producer.send(task.toTask(oldTask))
			logger.info("Updated task $newTask")
			return accepted().body(newTask)
		} else notFound().build()
	}

	@DeleteMapping("/{taskId}")
	fun delete(@PathVariable taskId: String): ResponseEntity<Void> {
		if (repository.has(taskId)) {
			producer.delete(taskId)
			return accepted().build()
		} else return notFound().build()
	}
}