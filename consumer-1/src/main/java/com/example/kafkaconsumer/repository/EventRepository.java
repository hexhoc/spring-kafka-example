package com.example.kafkaconsumer.repository;

import com.example.kafkaconsumer.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
