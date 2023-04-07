package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/produce")
public class ProducerController {
    private final ProducerService producerService;

    @GetMapping("/{topicName}")
    public ResponseEntity<String> produce(@PathVariable(name = "topicName") String topicName) {
        producerService.produce(topicName);
        return ResponseEntity.ok("OK");
    }

}
