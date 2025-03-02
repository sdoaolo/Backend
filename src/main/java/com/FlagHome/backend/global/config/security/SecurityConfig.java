package com.FlagHome.backend.global.config.security;

import com.FlagHome.backend.global.config.JwtSecurityConfig;
import com.FlagHome.backend.global.jwt.JwtAccessDeniedHandler;
import com.FlagHome.backend.global.jwt.JwtAuthenticationEntryPoint;
import com.FlagHome.backend.global.jwt.JwtUtilizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtilizer jwtUtilizer;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://flaground.netlify.app"));
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "PATCH", "DELETE"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**")
                .antMatchers("/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
            .exceptionHandling()
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .authenticationEntryPoint(jwtAuthenticationEntryPoint);

        http.authorizeRequests()
            .antMatchers("/auth/**").permitAll()

            .antMatchers(HttpMethod.GET, "/activities", "/activities/{id}").permitAll()
            .antMatchers("/activities/**").hasRole("CREW")

            .antMatchers(HttpMethod.GET, "/boards/**").permitAll()
            .antMatchers("/boards/**").hasRole("ADMIN")

            .antMatchers(HttpMethod.GET, "/members/{loginId}").permitAll()
            .antMatchers(HttpMethod.PUT, "/members/find/password").permitAll()
            .antMatchers(HttpMethod.POST, "/members/**").permitAll()
            .antMatchers("/members/**").hasAnyRole("USER", "CREW")

            .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
            .antMatchers("/posts/**").hasAnyRole("USER", "CREW")

            .antMatchers(HttpMethod.GET, "/replies/**").permitAll()
            .antMatchers("/replies/**").hasAnyRole("USER", "CREW")

            .antMatchers(HttpMethod.POST, "api/reports/**").hasAnyRole("USER", "CREW")
            .antMatchers("api/reports/**").hasRole("ADMIN")

            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated();

        http
            .apply(jwtSecurityConfig(jwtUtilizer));

        return http.build();
    }

    private JwtSecurityConfig jwtSecurityConfig(JwtUtilizer jwtUtilizer) {
        return new JwtSecurityConfig(jwtUtilizer);
    }
}
