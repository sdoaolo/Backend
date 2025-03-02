package com.FlagHome.backend.global.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    @ApiModelProperty(example = "LOGIN_FAILED")
    private ErrorCode errorCode;

    @ApiModelProperty(example = "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.")
    private String message;

    @Builder
    public ErrorResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
