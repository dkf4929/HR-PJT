package project.h5renewal.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.h5renewal.member.dto.MemberSaveDto;
import project.h5renewal.member.entity.Member;
import project.h5renewal.member.repository.MemberRepository;

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
                .role(param.getRole())
                .build();
    }
}
