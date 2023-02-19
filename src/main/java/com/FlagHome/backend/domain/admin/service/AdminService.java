package com.FlagHome.backend.domain.admin.service;

import com.FlagHome.backend.domain.auth.dto.ApproveSignUpResponse;
import com.FlagHome.backend.domain.auth.entity.AuthInformation;
import com.FlagHome.backend.domain.auth.repository.AuthRepository;
import com.FlagHome.backend.domain.member.avatar.service.AvatarService;
import com.FlagHome.backend.domain.member.dto.LoginLogResponse;
import com.FlagHome.backend.domain.member.entity.Member;
import com.FlagHome.backend.domain.member.repository.MemberRepository;
import com.FlagHome.backend.domain.member.service.MemberService;
import com.FlagHome.backend.global.exception.CustomException;
import com.FlagHome.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;

    @Transactional(readOnly = true)
    public List<ApproveSignUpResponse> getAllAuthorizedAuthMember() {
        return authRepository.getAllNeedApprovalAuthInformation();
    }

    @Transactional
    public void approveMember(long authInformationId) {
        AuthInformation authInformation = authRepository.findById(authInformationId)
                        .orElseThrow(() -> new CustomException(ErrorCode.AUTH_INFORMATION_NOT_FOUND));

        Member member = memberRepository.save(Member.of(authInformation, passwordEncoder));
        avatarService.initAvatar(member, authInformation.getNickName());
        deleteAuthInformation(authInformationId);
    }

    @Transactional
    public void deleteAuthInformation(long authInformationId) {
        authRepository.deleteById(authInformationId);
    }

    @Transactional
    public List<LoginLogResponse> viewAllLoginLog() {
        return memberService.getAllLoginLogs();
    }
}
