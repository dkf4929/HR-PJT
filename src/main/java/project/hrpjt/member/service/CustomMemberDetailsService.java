package project.hrpjt.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.hrpjt.exception.NoSuchMemberException;
import project.hrpjt.member.repository.MemberRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class CustomMemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);

        if (m.matches()) {
            return null;
        } else {
            return memberRepository.findByLoginId(username).orElseThrow(
                    () -> {
                        throw new NoSuchMemberException("가입되지 않은 사용자입니다.");
                    });
        }
    }
}
