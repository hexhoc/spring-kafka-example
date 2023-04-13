package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.entity.EventProcessing;
import com.example.kafkaproducer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProducerController {
    private final ProducerService producerService;

    @PostMapping("/produce/{topicName}")
    public ResponseEntity<String> sendMessage(
            @PathVariable(name = "topicName") String topicName,
            @RequestBody String content) {
        producerService.produce(topicName, content);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/produce/highload/{topicName}")
    public ResponseEntity<String> sendBatchMessages(
            @PathVariable(name = "topicName") String topicName,
            @RequestParam(name = "messageCount", defaultValue = "1000") Integer messageCount,
            @RequestParam(name = "startValue", defaultValue = "0") Integer startValue,
            @RequestParam(name = "timeGap", defaultValue = "0") Integer timeGap) {
        producerService.produceBatchMessages(topicName, messageCount, startValue, timeGap);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/data")
    public List<EventProcessing> getData() {
        return producerService.findAll();
    }
}
