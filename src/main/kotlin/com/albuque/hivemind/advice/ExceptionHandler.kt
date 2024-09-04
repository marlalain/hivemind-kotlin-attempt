package com.albuque.hivemind.advice

import com.albuque.hivemind.exceptions.ServerNotReadyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Duration

class HivemindError(
	val message: String,
	private val status: HttpStatus,
) {
	fun toResponseEntity(): ResponseEntity<HivemindError> = ResponseEntity(this, status)
	fun toResponseEntity(headers: HttpHeaders): ResponseEntity<HivemindError> = ResponseEntity(this, headers, status)
}

@ControllerAdvice
class HivemindExceptionHandler {
	@ExceptionHandler(ServerNotReadyException::class)
	fun serverNotReady(ex: ServerNotReadyException, request: WebRequest): ResponseEntity<HivemindError> {
		val headers = HttpHeaders()
		headers.add(HttpHeaders.RETRY_AFTER, Duration.ofMinutes(1).seconds.toString())

		return HivemindError(message = "Server not ready yet", HttpStatus.SERVICE_UNAVAILABLE).toResponseEntity(headers)
	}
}