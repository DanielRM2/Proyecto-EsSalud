package com.ed.ecommerce.mvcDemo.Controller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público a todas las rutas
                        .requestMatchers("/**").permitAll()
                )
                .formLogin().disable() // Deshabilitar el formulario de inicio de sesión por defecto
                .httpBasic().disable(); // Deshabilitar la autenticación básica

        return http.build();
    }
}
