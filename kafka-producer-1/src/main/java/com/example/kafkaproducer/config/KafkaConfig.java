package com.example.kafkaproducer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    public static final String TOPIC_ALL_REQUEST = "topic_all_request";
    public static final String TOPIC_ALL_RESPONSE = "topic_all_response";

    public static final String TOPIC_1_REQUEST = "topic_1_request";
    public static final String TOPIC_2_REQUEST = "topic_2_request";
    public static final String TOPIC_1_RESPONSE = "topic_1_response";
    public static final String TOPIC_2_RESPONSE = "topic_2_response";

    private final KafkaProperties kafkaProperties; // Autowired. Get data from application.yaml (prefix = "spring.kafka")

    @Bean
    public Map<String, Object> producerConfigs() {
        // The Producer Configuration is a simple key-value map
        // we build our map passing the default values for the producer and overriding the default Kafka key and value serializers.
        // The producer will serialize keys as Strings using the Kafka library’s StringSerializer and will do the same
        // for values but this time using JSON, with a JsonSerializer, in this case provided by Spring Kafka.
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // This is the object we employ to send messages to Kafka
        // the KafkaTemplate to have a plain String key, and an Object as value. The content to have Object as a value
        // is that we want to send multiple object types with the same template.
        return new KafkaTemplate<>(producerFactory());
    }

    // When we inject a NewTopic bean, we’re instructing the Kafka’s AdminClient bean (already in the context)
    // to create a topic with the given configuration
    @Bean
    public NewTopic Topic1Request() {
        return TopicBuilder.name(TOPIC_1_REQUEST).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic Topic2Request() {
        return TopicBuilder.name(TOPIC_2_REQUEST).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic Topic1Response() {
        return TopicBuilder.name(TOPIC_1_RESPONSE).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic Topic2Response() {
        return TopicBuilder.name(TOPIC_2_RESPONSE).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic TopicAllRequest() {
        return TopicBuilder.name(TOPIC_ALL_REQUEST).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic TopicAllResponse() {
        return TopicBuilder.name(TOPIC_ALL_RESPONSE).partitions(1).replicas(1).build();
    }
}