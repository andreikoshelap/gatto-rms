package com.gatto.consumer.repository;

import com.gatto.consumer.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
}
