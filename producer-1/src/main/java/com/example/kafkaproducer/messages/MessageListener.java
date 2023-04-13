package com.example.kafkaproducer.messages;

import com.example.kafkaproducer.config.KafkaConfig;
import com.example.kafkaproducer.entity.EventProcessing;
import com.example.kafkaproducer.messages.payload.event.TopicEventPayload;
import com.example.kafkaproducer.repository.EventProcessingRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Log4j2
public class MessageListener {
    private final ObjectMapper objectMapper;
    private final EventProcessingRepository eventProcessingRepository;

    @KafkaListener(id = "producer_1", groupId = "producer_1", topics = {KafkaConfig.TOPIC_1_RESPONSE, KafkaConfig.TOPIC_2_RESPONSE, KafkaConfig.TOPIC_ALL_RESPONSE})
    public void messageReceiver(String messagePayloadJson, @Header("type") String messageType) throws Exception {
//        log.info("MESSAGE TYPE: " + messageType);
        Message<TopicEventPayload> message = objectMapper.readValue(messagePayloadJson, new TypeReference<>() {});
        if ("consumer_1_event".equals(messageType)) {
            log.info("GET FROM CONSUMER-1: " + message.getData().content());
        } else if ("consumer_2_event".equals(messageType)) {
            log.info("GET FROM CONSUMER-2: " + message.getData().content());
        } else {
            log.info("Ignored message of type " + messageType);
        }

        processMessage(message.getTraceid(), message.getData().content());
    }

    public void processMessage(String messageId, String data) {
        writeToFile(data);
        writeToDb(messageId, data);
    }

    private void writeToFile(String data) {
        // write date to file
        try {
            FileWriter writer = new FileWriter("producer-1/file.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToDb(String messageId, String data) {
        Optional<EventProcessing> eventOptional = eventProcessingRepository.findByMessageId(messageId);
        if (eventOptional.isPresent()) {
            var eventProcessing = eventOptional.get();
            eventProcessing.setStatus("PROCESSED");
            eventProcessingRepository.save(eventProcessing);
        }
    }
}
