package com.FlagHome.backend.domain.activity.entity;

import com.FlagHome.backend.domain.BaseEntity;
import com.FlagHome.backend.domain.activity.ActivityType;
import com.FlagHome.backend.domain.activity.Proceed;
import com.FlagHome.backend.domain.activity.Status;
import com.FlagHome.backend.domain.activity.dto.ActivityRequest;
import com.FlagHome.backend.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Activity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member leader;

    @Column
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column
    @Enumerated(EnumType.STRING)
    private Proceed proceed;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private String season;

    public Activity(Long id, String name, String description, Member leader,
                    ActivityType activityType, Proceed proceed, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.activityType = activityType;
        this.proceed = proceed;
        this.status = status;
        this.season = getSeason();
    }

    public void setLeader(Member member) {
        this.leader = member;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
    public void update(ActivityRequest activityRequest) {
        this.name = activityRequest.getName();
        this.description = activityRequest.getDescription();
        this.proceed = activityRequest.getProceed();
    }

    protected String getSeason() { // 꼭 개선하기
        final int month = LocalDateTime.now().getMonthValue();

        if (3 <= month && month < 6) {
            return "1학기";
        } else if (6 <= month && month < 9) {
            return "여름방학";
        } else if (9 <= month && month < 12) {
            return "2학기";
        } else {
            return "겨울방학";
        }
    }
}