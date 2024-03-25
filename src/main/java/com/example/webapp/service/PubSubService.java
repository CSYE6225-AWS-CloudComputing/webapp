package com.example.webapp.service;

import com.example.webapp.controlleradvice.PubSubException;
import com.example.webapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class PubSubService {

    private final Logger logger = LogManager.getLogger(PubSubService.class);

    @Value("${projectID}")
    private String projectId;

    @Value("${topicName}")
    private String topicName;

    public void publishMessageToCloudFunction(ResponseEntity<User> userResponseEntity) throws IOException {

        // Create a Pub/Sub publisher
        Publisher publisher = Publisher.newBuilder(TopicName.of(projectId, topicName))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonData = objectMapper.writeValueAsString(userResponseEntity.getBody());

        // Create a Pub/Sub message with the payload
        ByteString data = ByteString.copyFromUtf8(jsonData);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

        // Publish the message to the Pub/Sub topic
        publisher.publish(pubsubMessage);
        logger.info("Message Published");

        // Shutdown the publisher
        publisher.shutdown();
    }
}
