package project.hrpjt.login.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.hrpjt.exception.NoSuchMemberException;
import project.hrpjt.login.dto.LoginParamDto;
import project.hrpjt.login.kakaologinmanager.AccessToken;
import project.hrpjt.member.entity.Member;
import project.hrpjt.member.repository.MemberRepository;
import project.hrpjt.tokenmanager.JwtTokenProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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

        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String kakaoLogin(String code, HttpServletResponse response) throws ParseException {
        String kakaoAccessToken = token.getKakaoAccessToken(code);
        Map<String, Object> userInfo = null;

        try {
            userInfo = token.getUserInfo(kakaoAccessToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return getRedirectURI(response, getEmail(userInfo), getId(userInfo));
    }

    private String getRedirectURI(HttpServletResponse response, String email, String kakaoId) {
        String redirectURI = "";

        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);

        if (member.isPresent()) {
            List<String> list = new ArrayList<String>();
            list.add("ROLE_USER");

            String kakaoToken = jwtTokenProvider.createToken(member.get().getKakaoMail(), list);

            Cookie cookie = new Cookie("jwtToken", kakaoToken);
            cookie.setPath("/");
            response.addCookie(cookie);
            redirectURI = "/";
        } else {
            redirectURI = "/members/add?kakaoId=" + kakaoId + "&kakaoMail=" + email;
        }
        return redirectURI;
    }

    private String getEmail(Map<String, Object> map) throws ParseException {
        JSONParser parser = new JSONParser();

        String account = String.valueOf(map.get("account"));

        JSONObject jsonObj = (JSONObject) parser.parse(account);

        return String.valueOf(jsonObj.get("email"));
    }

    private String getId(Map<String, Object> map) throws ParseException {
        JSONParser parser = new JSONParser();

        return String.valueOf(map.get("id"));
    }
}
