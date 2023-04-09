package com.example.kafkaconsumer.messages.payload.event;

public record TopicEventPayload(
        String refId,
        String content) { }
