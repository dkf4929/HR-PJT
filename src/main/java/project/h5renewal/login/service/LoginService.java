package project.h5renewal.login.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import project.h5renewal.exception.NoSuchMemberException;
import project.h5renewal.login.dto.LoginParamDto;
import project.h5renewal.login.kakaologinmanager.AccessToken;
import project.h5renewal.member.entity.Member;
import project.h5renewal.member.repository.MemberRepository;
import project.h5renewal.tokenmanager.JwtTokenProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccessToken token;
    private final PasswordEncoder encoder;

    public void login(LoginParamDto param, HttpServletResponse response) {
        Member member = memberRepository.findByLoginId(param.getLoginId())
                .orElseThrow(() -> {
                    throw new NoSuchMemberException("등록된 아이디가 없습니다.");
                });

        if (!encoder.matches(param.getPassword(), member.getPassword())) {
            throw new NoSuchMemberException("패스워드가 일치하지 않습니다.");
        }

        List<String> list = new ArrayList<>();
        list.add(member.getRole());
        String token = jwtTokenProvider.createToken(param.getLoginId(), list);

        Cookie cookie = new Cookie("token", token);
        response.addCookie(cookie);
    }

    public void kakaoLogin(String code, Model model) {
        String kakaoAccessToken = token.getKakaoAccessToken(code);
        Map<String, Object> userInfo = null;

        try {
            userInfo = token.getUserInfo(kakaoAccessToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("code", code);
        model.addAttribute("access_token", kakaoAccessToken);
        model.addAttribute("userInfo", userInfo);
    }
}
