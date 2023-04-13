package com.example.kafkaproducer.repository;

import com.example.kafkaproducer.entity.EventProcessing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventProcessingRepository extends JpaRepository<EventProcessing, Long> {
    Optional<EventProcessing> findByMessageId(String messageId);
}
