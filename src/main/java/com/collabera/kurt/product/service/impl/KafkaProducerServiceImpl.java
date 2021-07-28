package com.collabera.kurt.product.service.impl;

import com.collabera.kurt.product.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    public static final String TOPIC = "productTopics";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Kafka Service to publish topic and message
     * @param message
     */
    @Override
    public void publishToTopic(String message) {
        log.info("Publishing to {}, {}", TOPIC, message);
        kafkaTemplate.send(TOPIC, message);
    }
}
