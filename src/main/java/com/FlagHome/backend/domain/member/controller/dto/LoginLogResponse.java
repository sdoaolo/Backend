package com.FlagHome.backend.domain.member.controller.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
public class LoginLogResponse {

    @Schema(name = "아이디")
    private long id;

    @Schema(name = "이름", example = "김민정")
    private String name;

    @Schema(name = "마지막 로그인 시간")
    private LocalDateTime lastLoginTime;

    @Builder
    @QueryProjection
    public LoginLogResponse(long id, String name, LocalDateTime lastLoginTime) {
        this.id = id;
        this.name = name;
        this.lastLoginTime = lastLoginTime;
    }
}
