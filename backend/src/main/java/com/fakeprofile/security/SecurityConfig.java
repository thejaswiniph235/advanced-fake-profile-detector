package com.fakeprofile.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtFilter filter) throws Exception {
        http.csrf(c->c.disable())
            .cors(c->c.configurationSource(cors()))
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a
                .requestMatchers("/api/auth/**","/actuator/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(filter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource cors() {
        CorsConfiguration c=new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173"));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**",c);
        return s;
    }
}
