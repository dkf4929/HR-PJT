package project.hrpjt.login.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.hrpjt.employee.entity.Employee;
import project.hrpjt.exception.NoSuchEmployeeException;
import project.hrpjt.login.dto.LoginParamDto;
import project.hrpjt.mail.MailSender;
import project.hrpjt.security.kakaologinmanager.AccessToken;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.security.tokenmanager.JwtTokenProvider;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {
    private final EmployeeRepository employeeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccessToken token;
    private final PasswordEncoder encoder;

    public void passwordSearch(String empNo) {
        List<String> contents = new ArrayList<>();
        List<String> recipient = new ArrayList<>();

        Employee employee = employeeRepository.findByEmpNo(empNo).orElseThrow(() -> {
            throw new NoSuchEmployeeException("등록된 아이디가 없습니다.");
        });

        String randomPassword = getRandomPassword(10);
        String encodedRandomPassword = encoder.encode(randomPassword);

        employee.updatePassword(encodedRandomPassword);

        contents.add("임시 비밀번호 : " + randomPassword);
        recipient.add(employee.getExternalMail());

        MailSender.sendMail("임시 비밀번호 발급", contents, recipient);
    }

    public String login(LoginParamDto param, HttpServletResponse response) {
        Employee employee = employeeRepository.findByEmpNo(param.getEmpNo())
                .orElseThrow(() -> {
                    throw new NoSuchEmployeeException("등록된 아이디가 없습니다.");
                });

        if (!encoder.matches(param.getPassword(), employee.getPassword())) {
            throw new NoSuchEmployeeException("패스워드가 일치하지 않습니다.");
        }

        List<String> list = new ArrayList<>();
        list.add(employee.getRole());
        String token = jwtTokenProvider.createToken(param.getEmpNo(), list);

        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setMaxAge(30 * 60 * 1000);
        cookie.setPath("/");
        response.addCookie(cookie);

        return token;
    }

    public String kakaoLogin(String code, HttpServletResponse response) {
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

        Optional<Employee> employee = employeeRepository.findByKakaoId(kakaoId);

        if (employee.isPresent()) {
            List<String> list = new ArrayList<String>();
            list.add("role-empLOYEE");

            String kakaoToken = jwtTokenProvider.createToken(employee.get().getKakaoMail(), list);

            Cookie cookie = new Cookie("jwtToken", kakaoToken);
            cookie.setPath("/");
            response.addCookie(cookie);
            redirectURI = "/";
        } else {
            redirectURI = "/employees/add?kakaoId=" + kakaoId + "&kakaoMail=" + email;
        }
        return redirectURI;
    }

    private String getEmail(Map<String, Object> map) {
        JSONParser parser = new JSONParser();

        String account = String.valueOf(map.get("account"));

        JSONObject jsonObj = null;
        try {
            jsonObj = (JSONObject) parser.parse(account);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return String.valueOf(jsonObj.get("email"));
    }

    private String getId(Map<String, Object> map) {
        JSONParser parser = new JSONParser();

        return String.valueOf(map.get("id"));
    }

    public String getRandomPassword(int size) {
        char[] charSet = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^', '&' };

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        int idx = 0;
        int len = charSet.length;
        for (int i=0; i<size; i++) {
            idx = sr.nextInt(len);    // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
            sb.append(charSet[idx]);
        }

        return sb.toString();
    }
}
