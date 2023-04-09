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

    public void produce(String topicName, String content) {
        log.info("SEND TO " + topicName);

        String messageType;
        if (topicName.equals(KafkaConfig.TOPIC_ALL_REQUEST)) {
            messageType = "for_all_consumer_command";
        } else {
            messageType = "concrete_consumer_command";
        }

        messageSender.send(
                new Message<>(
                        messageType,
                        UUID.randomUUID().toString(),
                        new TopicCommandPayload(UUID.randomUUID().toString(), content)),
                topicName);
    }
}
