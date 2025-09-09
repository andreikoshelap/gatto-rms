package com.gatto.consumer.repository;

import com.gatto.consumer.entity.ResourceChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceChangeRepository extends JpaRepository<ResourceChange, Long> {}
