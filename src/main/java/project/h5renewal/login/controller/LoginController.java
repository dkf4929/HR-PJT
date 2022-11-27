package project.h5renewal.login.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import project.h5renewal.login.dto.LoginParamDto;
import project.h5renewal.login.kakaologinmanager.AccessToken;
import project.h5renewal.login.service.LoginService;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final AccessToken token;

    @PostMapping("/login")
    @ResponseBody
    public String login(LoginParamDto param, HttpServletResponse response) {
        loginService.login(param, response);
        return "로그인 완료";
    }

    @GetMapping("/kakao")
    public String kakaoForm() {
        return "kakaoLogin";
    }

    @GetMapping("/login/kakao")
    public String kakaoLogin(String code, Model model) {
        loginService.kakaoLogin(code, model);
        return "loginResult";
    }

    @GetMapping("/test")
    public String test() {
        return "토큰 인증 성공";
    }
}
