package com.albuque.hivemind.config

import com.albuque.hivemind.entities.tasks.Task
import com.albuque.hivemind.entities.tasks.TaskRepository
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
@EnableKafka
class KafkaStreamConfig {
	companion object {
		val logger: Logger = getLogger(KafkaStreamConfig::class.java)
		val props = mapOf(
			StreamsConfig.APPLICATION_ID_CONFIG to "hivemind",
			StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
			StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name,
			StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to JsonSerde::class.java.name,
			"group.id" to "hivemind",
			"key.serializer" to StringSerializer::class.java.name,
			"value.serializer" to JsonSerializer::class.java.name,
			"key.deserializer" to StringDeserializer::class.java.name,
			"value.deserializer" to JsonDeserializer::class.java.name,
			"spring.json.trusted.packages" to "*"
		)
	}

	@Bean
	@Primary
	fun kafkaStreams(
		@Qualifier("topology") topology: Topology,
	): KafkaStreams {
		logger.debug("Creating kafka streams")

		return KafkaStreams(topology, StreamsConfig(props)).apply {
			Runtime.getRuntime().addShutdownHook(Thread(this::close))
		}
	}

	@Bean
	@Primary
	fun topology(streamsBuilder: StreamsBuilder): Topology {
		logger.info("Creating topology")

		val taskStream =
			streamsBuilder.stream(TaskRepository.TOPIC_NAME, Consumed.with(StringSerde(), JsonSerde(Task::class.java)))
		taskStream.to(TaskRepository.STREAM_NAME, Produced.with(StringSerde(), JsonSerde(Task::class.java)))
		taskStream.foreach { key, value -> logger.info("Streaming $key -> $value") }

		return streamsBuilder.build()
	}

	@Bean
	@Primary
	fun streamsBuilderFactoryBean(): StreamsBuilderFactoryBean {
		return StreamsBuilderFactoryBean(KafkaStreamsConfiguration(props))
	}
}