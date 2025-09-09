package com.gatto.consumer.repository;

import com.gatto.consumer.entity.RawJson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawJsonRepository extends JpaRepository<RawJson, Long> {
    boolean existsByTopicAndPartitionAndOffset(String topic, Integer partition, Long offset);
}
