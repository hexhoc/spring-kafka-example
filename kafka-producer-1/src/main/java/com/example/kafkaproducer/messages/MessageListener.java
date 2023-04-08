package com.example.kafkaproducer.messages;

import com.example.kafkaproducer.config.KafkaConfig;
import com.example.kafkaproducer.messages.payload.event.TopicEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Log4j2
public class MessageListener {
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(id = "producer_1", topics = {KafkaConfig.TOPIC_1_RESPONSE, KafkaConfig.TOPIC_2_RESPONSE})
    public void messageReceiver(String messagePayloadJson, @Header("type") String messageType) throws Exception {
        log.info(messageType);
        if ("Topic1Event".equals(messageType)) {
            topic1Received(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {
            }));
        } else if ("Topic2Event".equals(messageType)) {
            topic2Received(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {
            }));
        } else {
            log.info("Ignored message of type " + messageType);
        }
    }

    public void topic1Received(Message<TopicEventPayload> message) {
        log.info("producer-1 get from consumer-1");
        log.info("Correlated " + message);
    }

    public void topic2Received(Message<TopicEventPayload> message) {
        log.info("producer-1 get from consumer-2");
        log.info("Correlated " + message);
    }
}
