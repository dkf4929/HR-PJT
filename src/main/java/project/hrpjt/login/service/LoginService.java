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
import project.hrpjt.security.kakaologinmanager.AccessToken;
import project.hrpjt.employee.repository.EmployeeRepository;
import project.hrpjt.security.tokenmanager.JwtTokenProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {
    private final EmployeeRepository employeeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccessToken token;
    private final PasswordEncoder encoder;

    public void login(LoginParamDto param, HttpServletResponse response) {
        Employee employee = employeeRepository.findByemployeeNo(param.getEmployeeNo())
                .orElseThrow(() -> {
                    throw new NoSuchEmployeeException("등록된 아이디가 없습니다.");
                });

        if (!encoder.matches(param.getPassword(), employee.getPassword())) {
            throw new NoSuchEmployeeException("패스워드가 일치하지 않습니다.");
        }

        List<String> list = new ArrayList<>();
        list.add(employee.getRole());
        String token = jwtTokenProvider.createToken(param.getEmployeeNo(), list);

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

        Optional<Employee> employee = employeeRepository.findByKakaoId(kakaoId);

        if (employee.isPresent()) {
            List<String> list = new ArrayList<String>();
            list.add("ROLE_EMPLOYEE");

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
