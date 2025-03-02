package com.FlagHome.backend.global.infra.aws.ses;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MailType {
    AUTH_EMAIL("[FLAG] 재학생 이메일 인증 요청입니다.",
            "<div>서비스를 이용하기 위해서 재학생 인증이 필요합니다.<br>" +
                    "다음 6자리 인증번호를 입력해주세요.</div>"),

    FIND_AUTH("[FLAG] 아이디/비밀번호 찾기 이메일 인증 요청입니다.",
            "<div>아이디/비밀번호 찾기 이메일 인증 요청입니다.<br>" +
                    "다음 6자리 인증번호를 입력해주세요.</div>"),

    SLEEP_EMAIL("[FLAG] 휴면계정 전환 안내 메일입니다.",
            "일주일간 홈페이지 서비스 이용이 없으셨던 회원님의 계정이 휴면계정으로 전환될 예정입니다");

    private final String subject;
    private final String content;

    public String getSubject() { return this.subject; }

    public String createMailForm(String result) {
        return "<div>안녕하세요. 수원대 최고의 개발동아리 FLAG입니다.</div>" +
                this.content +
                "<div>" + result + "</div>" +
                "<div>추가적인 문의사항이 있으시다면, gmlwh124@naver.com 으로 연락 바랍니다.<br>" +
                "감사합니다.</div>";
    }

    public String createMailForm() {
        return "<div>안녕하세요. 수원대 최고의 개발동아리 FLAG입니다.</div>" +
                this.content +
                "<div>추가적인 문의사항이 있으시다면, gmlwh124@naver.com 으로 연락 바랍니다.<br>" +
                "감사합니다.</div>";
    }
}
