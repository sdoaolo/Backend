package com.FlagHome.backend.domain.auth.dto;

import com.FlagHome.backend.domain.auth.JoinType;
import com.FlagHome.backend.domain.member.Major;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {
    @ApiModelProperty(example = "gmlwh124")
    private String loginId;

    @ApiModelProperty(example = "1234")
    private String password;

    @ApiModelProperty(example = "문희조")
    private String name;

    @ApiModelProperty(value = "학교 이메일", example = "gmlwh124@suwon.ac.kr")
    private String email;

    @ApiModelProperty(example = "컴퓨터SW")
    private Major major;

    @ApiModelProperty(example = "19017041")
    private String studentId;

    @ApiModelProperty(value = "유저 가입 구분", notes = "일반 유저 : NORMAL, 동아리원 : CLUB")
    private JoinType joinType;
}
