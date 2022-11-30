package project.hrpjt.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.hrpjt.filter.JwtAuthenticationFilter;
import project.hrpjt.tokenmanager.JwtTokenProvider;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final String[] whiteList = {"/", "/login", "/members/add", "/login/kakao", "/kakao", "/logout"};  // security 적용 안할 path 설정

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http.csrf().disable();
        http.httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers(whiteList).permitAll() // 인증 허용 경로 설정
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) //jwt 토큰 인증 필터
                .logout().logoutSuccessHandler((request, response, authentication) -> {
                    response.sendRedirect("/");
                }) // logout -> redirect:/
                .logoutUrl("/logout")
                .deleteCookies("jwtToken")  // logout -> delete jwt cookie
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, exception) -> {
                    response.sendRedirect("/");
                })
                .accessDeniedHandler((request, response, exception) -> {
                    response.sendRedirect("/");
                });

        return http.build();
    }
}
