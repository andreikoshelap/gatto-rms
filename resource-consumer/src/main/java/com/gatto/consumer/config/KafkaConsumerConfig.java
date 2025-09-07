package com.gatto.consumer.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

// All comments in English
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            KafkaTemplate<String, String> dltTemplate // for dead-letter publishing (optional)
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);

        // Manual ack so we control when offset is committed
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // Optional: batch consume
        // factory.setBatchListener(true);

        // Error handling with DLT
        var recoverer = new DeadLetterPublishingRecoverer(dltTemplate,
                (rec, ex) -> new TopicPartition(rec.topic() + ".DLT", rec.partition()));
        var backoff = new FixedBackOff(1000L, 3L); // 3 retries with 1s delay
        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer, backoff));

        // Let’s keep commit sync to be safe in dev
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }
}
