package com.example.kafkaconsumer.messages;

import com.example.kafkaconsumer.config.KafkaConfig;
import com.example.kafkaconsumer.messages.payload.event.TopicEventPayload;
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
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(id = "consumer_1", topics = KafkaConfig.TOPIC_REQUEST)
    public void messageReceiver(String messagePayloadJson, @Header("type") String messageType) throws Exception {
        topicReceived(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {
        }));
    }

    public void topicReceived(Message<TopicEventPayload> message) {
        log.info("consumer-1 get from producer-1");
        log.info("Correlated " + message);

        messageSender.send(
                new Message<>(
                        "Topic1Event",
                        message.getTraceid(),
                        new TopicEventPayload(message.getData().refId())),
                KafkaConfig.TOPIC_RESPONSE
        );
    }

}
