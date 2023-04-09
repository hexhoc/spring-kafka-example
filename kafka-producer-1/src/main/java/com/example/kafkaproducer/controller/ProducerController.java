package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/produce")
public class ProducerController {
    private final ProducerService producerService;

    @PostMapping("/{topicName}")
    public ResponseEntity<String> sendMessage(
            @PathVariable(name = "topicName") String topicName,
            @RequestBody String content) {
        producerService.produce(topicName, content);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/highload/{topicName}")
    public ResponseEntity<String> sendBatchMessages(
            @PathVariable(name = "topicName") String topicName,
            @RequestParam(name = "messageCount", defaultValue = "1000") Integer messageCount,
            @RequestParam(name = "timeGap", defaultValue = "0") Integer timeGap) {
        producerService.produceBatchMessages(topicName, messageCount, timeGap);
        return ResponseEntity.ok("OK");
    }
}
