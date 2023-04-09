package com.example.kafkaconsumer.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Helper to send messages, currently nailed to Kafka, but could also send via AMQP (e.g. RabbitMQ) or
 * any other transport easily
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class MessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void send(Message<?> m, String topicName) {
        try {
            // avoid too much magic and transform ourselves
            String jsonMessage = objectMapper.writeValueAsString(m);

            // wrap into a proper content for Kafka including a header
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, jsonMessage);
            record.headers().add("type", m.getType().getBytes());

            // and send it
            var future = kafkaTemplate.send(record);
            future.whenComplete((result, throwable) -> {
                log.info("Message [{}] delivered with offset {}",
                        record,
                        result.getRecordMetadata().offset());
            });
        } catch (Exception e) {
            throw new RuntimeException("Could not transform and send content: " + e.getMessage(), e);
        }
    }
}
