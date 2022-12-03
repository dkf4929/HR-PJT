package project.hrpjt.security.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.hrpjt.security.filter.JwtAuthenticationFilter;
import project.hrpjt.security.oauth.service.UserOAuth2Service;
import project.hrpjt.security.tokenmanager.JwtTokenProvider;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final String[] whiteList = {"/", "/login", "/login/kakao", "/kakao", "/logout"};  // security 적용 안할 path 설정
    private final String[] adminPath = {"/employees/all", "/employees/*/delete", "/employees/add"}; // 관리자 허용 path
    private final String[] userPath = {"/employees/**"}; // user 허용 path
    private final UserOAuth2Service userOAuth2Service;
    private final AccessDeniedHandler accessDeniedHandler;

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
                .requestMatchers(adminPath).hasRole("ADMIN")
                .requestMatchers(userPath).hasAnyRole("USER", "ADMIN")
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
                    log.error("인증 에러");
                    response.sendRedirect("/");
                })
                .accessDeniedHandler(accessDeniedHandler);
//                .and()
//                .oauth2Login().defaultSuccessUrl("/test").successHandler(((request, response, authentication) -> {
//                    System.out.println("success!!");
//                })).failureHandler(((request, response, exception) -> {
//                    System.out.println("fail");
//                })).userInfoEndpoint().userService(userOAuth2Service);

        return http.build();
    }
}
