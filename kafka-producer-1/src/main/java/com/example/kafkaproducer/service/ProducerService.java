package com.example.kafkaproducer.service;

import com.example.kafkaproducer.config.KafkaConfig;
import com.example.kafkaproducer.messages.Message;
import com.example.kafkaproducer.messages.MessageSender;
import com.example.kafkaproducer.messages.payload.command.TopicCommandPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProducerService {
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    public void produce(String topicName) {
        log.info("Produce messages");
        if (topicName.equals("all")) {
            sendMessage(KafkaConfig.TOPIC_1_REQUEST);
            sendMessage(KafkaConfig.TOPIC_2_REQUEST);
        } else {
            sendMessage(topicName);
        }
    }

    private void sendMessage(String topicName) {
        log.info("producer-1 send to " + topicName);

        messageSender.send(
                new Message<>(
                        "TopicCommand",
                        UUID.randomUUID().toString(),
                        new TopicCommandPayload(UUID.randomUUID().toString(), "TEST")),
                topicName);
    }
}
