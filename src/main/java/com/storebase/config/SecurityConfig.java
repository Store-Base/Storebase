package com.storebase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// PROVISÓRIO: libera todos os endpoints. Existe apenas para que a aplicação
// continue funcionando depois que o spring-boot-starter-security entrou no
// classpath (a autoconfiguração de segurança bloquearia tudo por padrão).
// Será substituída em P0.3 (feature/autenticacao-jwt) pela regra de
// autorização real por perfil.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
