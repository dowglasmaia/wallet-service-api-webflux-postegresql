package com.dowglasmaia.wallet.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;

@RequiredArgsConstructor
@Configuration
public class KafkaAdminConfig {
    private final KafkaProperties properties;
    private static final int PARTITION = 1;
    private static final int REPLICA = 1;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        var props = new HashMap<String, Object>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        return new KafkaAdmin(props);
    }

    @Bean
    public KafkaAdmin.NewTopics newTopics(){
        return new KafkaAdmin.NewTopics(
              TopicBuilder.name("audit-transaction-topic")
                    .partitions(PARTITION)
                    .replicas(REPLICA)
                    .build()
        );
    }

}
