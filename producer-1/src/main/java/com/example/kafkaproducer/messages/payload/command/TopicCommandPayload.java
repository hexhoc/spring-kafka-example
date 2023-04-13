package com.example.kafkaproducer.messages.payload.command;

public record TopicCommandPayload(
        String refId,
        String content) { }
