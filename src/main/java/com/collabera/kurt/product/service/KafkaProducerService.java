package com.collabera.kurt.product.service;


public interface KafkaProducerService {

    void publishToTopic(String message);

}
