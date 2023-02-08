package com.FlagHome.backend.domain.member;

import com.FlagHome.backend.domain.Status;
import com.FlagHome.backend.domain.member.dto.UpdatePasswordRequest;
import com.FlagHome.backend.domain.member.dto.ViewLogResponse;
import com.FlagHome.backend.domain.member.entity.Member;
import com.FlagHome.backend.domain.member.repository.MemberRepository;
import com.FlagHome.backend.domain.member.service.MemberService;
import com.FlagHome.backend.global.exception.CustomException;
import com.FlagHome.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Transactional
@SpringBootTest
public class MemberServiceTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;

    @Autowired
    private EntityManager entityManager;
    
    @Nested
    @DisplayName("유저 탈퇴 테스트")
    class withdrawTest {
        @Test
        @DisplayName("유저 탈퇴 성공")
        void withDrawSuccessTest() {
            // given
            String loginId = "gmlwh124";
            String password = "1234";

            Member savedMember = memberRepository.saveAndFlush(Member.builder()
                            .loginId(loginId)
                            .password(passwordEncoder.encode(password))
                            .build());

            // when
            memberService.withdraw(savedMember.getId(), password);
            entityManager.flush();

            // then : 정상적으로 탈퇴되어 멤버 정보가 레포에 없는지
            assertThatExceptionOfType(CustomException.class)
                    .isThrownBy(() -> memberRepository.findById(savedMember.getId())
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
        }

        @Test
        @DisplayName("비밀번호 불일치로 유저 탈퇴 실패")
        void withdrawFailTest() {
            String loginId = "gmlwh124";
            String password = "1234";
            String wrongPassword = "2345";

            Member saveMember =  memberRepository.saveAndFlush(Member.builder()
                            .loginId(loginId)
                            .password(passwordEncoder.encode(password))
                            .status(Status.GENERAL)
                            .build());

            assertThatExceptionOfType(CustomException.class)
                    .isThrownBy(() -> memberService.withdraw(saveMember.getId(), wrongPassword))
                    .withMessage(ErrorCode.PASSWORD_NOT_MATCH.getMessage());
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 테스트 - 잊어서 바꾸는 경우")
    class changePasswordTest {
        @Test
        @DisplayName("비밀번호 변경 성공")
        void changePasswordSuccessTest() {
            // given
            String email = "gmlwh124@suwon.ac.kr";
            String newPassword = "qwer1234!";

            Member member = memberRepository.save(Member.builder().email(email).build());

            // when
            memberService.changePassword(email, newPassword);

            // then
            boolean check = passwordEncoder.matches(newPassword, member.getPassword());
            assertThat(check).isTrue();
        }

        @Test
        @DisplayName("비밀번호 유효성 검사 실패로 변경 실패")
        void changeFailByValidateFailTest() {
            String email = "gmlwh124@suwon.ac.kr";
            String wrongPassword = "123456";

            assertThatExceptionOfType(CustomException.class)
                    .isThrownBy(() -> memberService.changePassword(email, wrongPassword))
                    .withMessage(ErrorCode.INVALID_PASSWORD.getMessage());
        }
    }


    @Nested
    @DisplayName("비밀번호 변경 테스트 - 유저가 바꾸는 경우")
    class updatePasswordTest {
        @Test
        @DisplayName("비밀번호 변경 성공")
        void updatePasswordSuccessTest() {
            // given
            String loginId = "gmlwh124";
            String password = "qwer1234!";
            String newPassword = "wert2345@";

            Member savedMember = memberRepository.save(Member.builder()
                            .loginId(loginId)
                            .password(passwordEncoder.encode(password))
                            .build());

            UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                            .currentPassword(password)
                            .newPassword(newPassword)
                            .build();

            // when
            memberService.updatePassword(savedMember.getId(), updatePasswordRequest);

            // then : 정상적으로 변경되었는고 같은 엔티티인지
            Member member = memberRepository.findById(savedMember.getId()).get();
            assertThat(member.getId()).isEqualTo(savedMember.getId());
            assertThat(member.getLoginId()).isEqualTo(loginId);
            boolean matches = passwordEncoder.matches(newPassword, member.getPassword());
            assertThat(matches).isTrue();
        }

        @Test
        @DisplayName("비밀번호 변경 중 같은 비밀번호로 실패")
        void updatePasswordFailTeset() {
            String loginId = "gmlwh124";
            String password = "qwer1234!";

            Member savedMember = memberRepository.save(Member.builder()
                    .loginId(loginId)
                    .password(passwordEncoder.encode(password))
                    .build());

            UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                    .currentPassword(password)
                    .newPassword(password)
                    .build();

            assertThatExceptionOfType(CustomException.class)
                    .isThrownBy(() -> memberService.updatePassword(savedMember.getId(), updatePasswordRequest))
                    .withMessage(ErrorCode.PASSWORD_IS_SAME.getMessage());
        }
    }

    @Test
    @DisplayName("로그보기")
    void viewLogTest() {
        //레포지토리에 정보를 저장하고 모든 로그를 가져오는 서비스를 호출해서 가져온 정보가 맞는지 확인하기?
        //given
        String loginId = "minjung123";
        String name = "김민정";
        LocalDateTime lastLoginTime = LocalDateTime.of(2023,2,5,2,59);

        Member member = memberRepository.save(Member.builder()
                .loginId(loginId)
                .name(name)
                .lastLoginTime(lastLoginTime)
                .build());

        //when : 모든 로그를 볼 수 있는 서비스 호출
        List<ViewLogResponse> memberList = memberService.viewLog();

        //then : 가져온 로그가 맞는지? 1. 리스트 형식으로 반환하니까 내가 넣은 개수만큼 리턴을 하는지 2. 아이디와 이름, 시간이 일치하는지
        ViewLogResponse testMember = memberList.get(0);

        assertThat(testMember.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(testMember.getName()).isEqualTo(member.getName());
        assertThat(testMember.getLastLoginTime()).isEqualTo(member.getLastLoginTime());
    }

//    @Nested
//    @DisplayName("프로필 가져오기 테스트")
//    class getProfileTest {
//        @Test
//        @DisplayName("유저 정보가 없어서 실패")
//        void getProfileFailTest() {
//            // given
//            String wrongLoginId = "hejow124";
//
//            assertThatExceptionOfType(CustomException.class)
//                    .isThrownBy(() -> memberService.getMemberProfile(wrongLoginId))
//                    .withMessage(ErrorCode.USER_NOT_FOUND.getMessage());
//        }
//
//        @Test
//        @DisplayName("프로필 가져오기 성공")
//        void getProfileSuccessTest() {
//            // given
//            String loginId = "gmlwh124";
//            String bio = "안녕하세요";
//            String profileImg = "url";
//
//            Member member = memberRepository.saveAndFlush(Member.builder()
//                            .loginId(loginId)
//                            .bio(bio)
//                            .profileImg(profileImg)
//                            .build());
//
//            // when
//            MyPageResponse profileResponse = memberService.getMyPage(loginId);
//
//            // then
//            assertThat(profileResponse.getBio()).isEqualTo(bio);
//            assertThat(profileResponse.getProfileImg()).isEqualTo(profileImg);
//        }
//    }
}
