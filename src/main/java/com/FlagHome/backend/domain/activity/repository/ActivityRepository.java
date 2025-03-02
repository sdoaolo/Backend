package com.FlagHome.backend.domain.activity.repository;

import com.FlagHome.backend.domain.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivityRepositoryCustom {
    Optional<Activity> findById(Long activityId);
    boolean existsById(Long activityId);
}