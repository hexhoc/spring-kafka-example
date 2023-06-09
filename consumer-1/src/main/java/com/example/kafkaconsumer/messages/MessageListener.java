package com.example.kafkaconsumer.messages;

import com.example.kafkaconsumer.config.KafkaConfig;
import com.example.kafkaconsumer.entity.Event;
import com.example.kafkaconsumer.messages.payload.command.TopicCommandPayload;
import com.example.kafkaconsumer.messages.payload.event.TopicEventPayload;
import com.example.kafkaconsumer.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;


@Component
@RequiredArgsConstructor
@Log4j2
public class MessageListener {

    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;
    private final EventRepository eventRepository;

    @KafkaListener(
            id = KafkaConfig.CONSUMER_ID,
            groupId = KafkaConfig.CONSUMER_GROUP,
            topics = {KafkaConfig.TOPIC_REQUEST, KafkaConfig.TOPIC_ALL_REQUEST}
    )
    public void messageReceiver(String messagePayloadJson, Acknowledgment acknowledgment, @Header("type") String messageType) throws Exception {
//        log.info("MESSAGE TYPE: " + messageType);
        if (messageType.equals("for_all_consumer_command")) {
            consumeMessage(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {}), KafkaConfig.TOPIC_ALL_RESPONSE);
        }else {
            consumeMessage(objectMapper.readValue(messagePayloadJson, new TypeReference<>() {}), KafkaConfig.TOPIC_RESPONSE);
        }

        //  manually acknowledgment
        acknowledgment.acknowledge();
    }

    public void consumeMessage(Message<TopicCommandPayload> message, String topicName) {
        log.info("GET FROM PRODUCER-1: " + message.getData().content());

        // PROCESS MESSAGE
        processMessage(message.getData().content());

        messageSender.send(
                new Message<>(
                        KafkaConfig.CONSUMER_ID + "_event",
                        message.getTraceid(),
                        new TopicEventPayload(message.getData().refId(), KafkaConfig.CONSUMER_ID+ ": " + message.getData().content())),
                topicName
        );
    }

    public void processMessage(String data) {
        writeToFile(data);
        writeToDb(data);
    }

    private void writeToFile(String data) {
        // write date to file
        try {
            FileWriter writer = new FileWriter("consumer-1/file.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToDb(String data) {
        Event event = new Event();
        event.setContent(data);
        eventRepository.save(event);
    }
}
