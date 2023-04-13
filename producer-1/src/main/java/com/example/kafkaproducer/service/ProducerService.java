package com.example.kafkaproducer.service;

import com.example.kafkaproducer.config.KafkaConfig;
import com.example.kafkaproducer.entity.EventProcessing;
import com.example.kafkaproducer.messages.Message;
import com.example.kafkaproducer.messages.MessageSender;
import com.example.kafkaproducer.messages.payload.command.TopicCommandPayload;
import com.example.kafkaproducer.repository.EventProcessingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProducerService {
    private final MessageSender messageSender;
    private final EventProcessingRepository eventProcessingRepository;

    public void produce(String topicName, String content) {
        log.info("SEND TO " + topicName);
        String messageId = UUID.randomUUID().toString();
        String messageType;
        if (topicName.equals(KafkaConfig.TOPIC_ALL_REQUEST)) {
            messageType = "for_all_consumer_command";
        } else {
            messageType = "concrete_consumer_command";
        }
        eventProcessingRepository.save(new EventProcessing(null, messageId, content, topicName, "NEW"));
        sendMessage(messageId, messageType, content, topicName);
    }

    public void produceBatchMessages(String topicName, Integer messageCount, Integer startValue, Integer timeGap) {
        String messageId = UUID.randomUUID().toString();
        String messageType = "for_all_consumer_command";
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < messageCount; i++) {
            eventProcessingRepository.save(new EventProcessing(null, messageId, String.valueOf(startValue + i), topicName, "NEW"));
            sendMessage(messageId, messageType, String.valueOf(startValue + i), topicName);
            if (timeGap > 0) {
                try {
                    Thread.sleep(timeGap);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }

    private void sendMessage(String messageId, String messageType, String content, String topicName) {
        messageSender.send(
                new Message<>(
                        messageType,
                        messageId,
                        new TopicCommandPayload(UUID.randomUUID().toString(), content)
                ),
                topicName);
    }

    public List<EventProcessing> findAll() {
        return eventProcessingRepository.findAll();
    }

}