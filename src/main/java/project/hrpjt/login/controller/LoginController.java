package project.hrpjt.login.controller;

import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import project.hrpjt.login.dto.LoginParamDto;
import project.hrpjt.security.kakaologinmanager.AccessToken;
import project.hrpjt.login.service.LoginService;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final AccessToken token;
    private final PasswordEncoder encoder;

    @PostMapping("/login")
    public String login(LoginParamDto param, HttpServletResponse response) {
        loginService.login(param, response);
        return "redirect:swagger-ui/index.html";
    }

    @GetMapping("/kakao")
    public String kakaoForm() {
        return "kakao/kakaoLogin";
    }

    @RequestMapping("/login/kakao")
    public String kakaoLogin(String code, HttpServletResponse response) throws ParseException {
        return "redirect:" + loginService.kakaoLogin(code, response);
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "토큰 인증 성공";
    }
}
