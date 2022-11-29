package project.hrpjt.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.hrpjt.member.dto.MemberSaveDto;
import project.hrpjt.member.entity.Member;
import project.hrpjt.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    public void save(MemberSaveDto param) {
        Member member = dtoToMember(param);

        memberRepository.save(member);
    }

    private Member dtoToMember(MemberSaveDto param) {
        return Member.builder()
                .loginId(param.getLoginId())
                .password(encoder.encode(param.getPassword()))
                .memberName(param.getMemberName())
                .kakaoMail(param.getKakaoMail())
                .role("ROLE_USER")
                .build();
    }
}
