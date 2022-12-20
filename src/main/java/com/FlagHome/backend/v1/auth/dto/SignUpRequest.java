package com.FlagHome.backend.v1.auth.dto;

import com.FlagHome.backend.v1.Status;
import com.FlagHome.backend.v1.member.Major;
import com.FlagHome.backend.v1.member.Role;
import com.FlagHome.backend.v1.member.entity.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
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

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .loginId(loginId)
                .password(passwordEncoder.encode(password))
                .email("")
                .name(name)
                .email(email)
                .major(major)
                .phoneNumber("")
                .studentId(studentId)
                .role(Role.USER)
                .status(Status.GENERAL)
                .build();
    }
}
