package com.albuque.hivemind.config

import com.albuque.hivemind.entities.tasks.Task
import com.albuque.hivemind.entities.tasks.TaskRepository
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.*
import java.util.UUID.randomUUID

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
			StreamsConfig.STATE_DIR_CONFIG to "/tmp/hivemind-kafka-state-${randomUUID()}",
			"group.id" to "hivemind-group",
			"key.serializer" to StringSerializer::class.java.name,
			"value.serializer" to JsonSerializer::class.java.name,
			"key.deserializer" to StringDeserializer::class.java.name,
			"value.deserializer" to JsonDeserializer::class.java.name,
			"spring.json.trusted.packages" to "*"
		)
	}

	@Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
	fun kafkaStreamsConfiguration(): KafkaStreamsConfiguration = KafkaStreamsConfiguration(props)

	@Bean
	fun streamsBuilder(): StreamsBuilder = StreamsBuilder(TopologyConfig(StreamsConfig(props)))

	@Bean(name = ["kafkaStreams"])
	fun kafkaStreams(streamsBuilder: StreamsBuilder): KafkaStreams {
		streamsBuilder.table(
			TaskRepository.TOPIC_NAME,
			Consumed.with(Serdes.String(), JsonSerde(Task::class.java)),
			Materialized.`as`(TaskRepository.TABLE_NAME)
		)
		val topology = streamsBuilder.build()
		val kafkaStreams = KafkaStreams(topology, props.toProperties())
		kafkaStreams.start()

		return kafkaStreams.apply { Runtime.getRuntime().addShutdownHook(Thread(this::close)) }
	}

	@Bean
	fun taskTopic(): NewTopic = NewTopic(TaskRepository.TOPIC_NAME, Optional.empty(), Optional.empty())
}