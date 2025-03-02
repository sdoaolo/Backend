package com.FlagHome.backend.domain.activity.memberactivity.repository;

import com.FlagHome.backend.domain.activity.entity.Activity;
import com.FlagHome.backend.domain.activity.memberactivity.entity.MemberActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberActivityRepository extends JpaRepository<MemberActivity, Long>, MemberActivityRepositoryCustom {
}
