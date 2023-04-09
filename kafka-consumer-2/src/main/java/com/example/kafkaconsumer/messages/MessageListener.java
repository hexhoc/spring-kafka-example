package com.example.kafkaconsumer.messages;

import com.example.kafkaconsumer.config.KafkaConfig;
import com.example.kafkaconsumer.messages.payload.command.TopicCommandPayload;
import com.example.kafkaconsumer.messages.payload.event.TopicEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


@Component
@RequiredArgsConstructor
@Log4j2
public class MessageListener {
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(id = "consumer_2", topics = {KafkaConfig.TOPIC_REQUEST, KafkaConfig.TOPIC_ALL_REQUEST})
    public void messageReceiver(String messagePayloadJson, @Header("type") String messageType) throws Exception {
//        log.info("MESSAGE TYPE: " + messageType);
        if (messageType.equals("for_all_consumer_command")) {
            topicReceived(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {}), KafkaConfig.TOPIC_ALL_RESPONSE);
        }else {
            topicReceived(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {}), KafkaConfig.TOPIC_RESPONSE);
        }
    }

    public void topicReceived(Message<TopicCommandPayload> message, String topicName) {
        log.info("GET FROM PRODUCER-1: " + message.getData().content());

        // PROCESS MESSAGE
        processMessage(message.getData().content());

        messageSender.send(
                new Message<>(
                        "Topic2Event",
                        message.getTraceid(),
                        new TopicEventPayload(message.getData().refId(), message.getData().content())),
                topicName
        );
    }

    public void processMessage(String data) {
        // write date to file
        try {
            FileWriter writer = new FileWriter("kafka-consumer-2/file.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
