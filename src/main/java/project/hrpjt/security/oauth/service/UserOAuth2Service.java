package project.hrpjt.security.oauth.service;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import project.hrpjt.employee.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class UserOAuth2Service extends DefaultOAuth2UserService {
    private final HttpSession httpSession;
    private final EmployeeRepository employeeRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return super.loadUser(userRequest);
    }
}
