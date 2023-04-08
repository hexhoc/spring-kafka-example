package com.example.kafkaconsumer.messages.payload.command;

public record TopicCommandPayload(
        String refId,
        String reason) { }
