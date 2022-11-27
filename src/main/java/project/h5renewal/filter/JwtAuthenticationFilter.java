package project.h5renewal.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import project.h5renewal.tokenmanager.JwtTokenProvider;
import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final String[] whiteList = {"/", "/login", "/members/add", "/login/kakao", "/kakao"}; // 토큰 인증 안할 경로 설정

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (!Arrays.stream(whiteList).anyMatch(a -> a.contains(req.getRequestURI()))) {
            //get token info
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

            if (token != null && jwtTokenProvider.validateToken(token)) { // 유효한 토큰인지 체크
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication); // security context에 인증 객체 저장
            }

            log.info("authentication info = {}", SecurityContextHolder.getContext().getAuthentication());
        }
        chain.doFilter(request, response);
    }
}