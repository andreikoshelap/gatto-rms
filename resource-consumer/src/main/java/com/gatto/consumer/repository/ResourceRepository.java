package com.gatto.consumer.repository;

import com.gatto.consumer.entity.ResourceReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceReadModel, Long> {
}
