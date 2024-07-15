package com.blog.auth.config;

import com.blog.auth.services.AuthFilterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//    private final AuthenticationProvider authenticationProvider;
//    private final AuthFilterService authFilterService;
//
//    @Bean
//    public SecurityConfiguration(AuthenticationProvider authenticationProvider, AuthFilterService authFilterService) {
//        this.authenticationProvider = authenticationProvider;
//        this.authFilterService = authFilterService;
//    }
//
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth").permitAll().anyRequest().authenticated())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class);
//                // xử lý các xác thực của JwtToken trước sau đó xử lý các xác thực trên username và password
//        return http.build();
//    }
//}
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final AuthFilterService authFilterService;

    public SecurityConfiguration(AuthenticationProvider authenticationProvider, AuthFilterService authFilterService) {
        this.authenticationProvider = authenticationProvider;
        this.authFilterService = authFilterService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**",
                        "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/api/v1/forgotPassword/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}