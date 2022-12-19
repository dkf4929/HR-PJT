package project.hrpjt.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.GenericFilterBean;
import project.hrpjt.security.tokenmanager.JwtTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final String[] whiteList = {"/login", "/login/kakao", "/kakao", ".ico", "/error", "/logout", "swagger"}; // 토큰 인증 안할 경로 설정

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String URI = req.getRequestURI();

        if (!Arrays.stream(whiteList).anyMatch(a -> URI.contains(a)) && !URI.equals("/")) {
            //get token info

            Cookie cookie = resolveToken((HttpServletRequest) request);

            String token = cookie.getValue();

            if (jwtTokenProvider.validateToken(token) && cookie.getName().equals("jwtToken")) { // 유효한 토큰인지 체크
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication); // security context에 인증 객체 저장
            }

                log.info("authentication info = {}", SecurityContextHolder.getContext().getAuthentication());

        }

        log.info("--------------jwt auth filter--------------");
        chain.doFilter(request, response);
    }

    // cookie => token value
    public Cookie resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        return Arrays.stream(cookies).filter((c) -> c.getName().equals("jwtToken") || c.getName().equals("kakaoToken"))
                .findFirst()
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("세션이 만료되었습니다.");
                });
    }
}