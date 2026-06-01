package uz.testplatform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security asosiy konfiguratsiyasi.
 *
 * - REST API uchun - sessiyasiz (STATELESS)
 * - CSRF o'chirilgan (REST API uchun kerak emas)
 * - JWT filter qo'shilgan
 * - Public endpoint'lar: auth/*, swagger
 * - ADMIN only: /admin/**
 * - Authenticated: hammasi qolgan
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;


    // Parol shifrlash - BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // AuthenticationManager - Spring'da login uchun
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


    // Asosiy filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF o'chirilgan - REST API uchun kerak emas
                .csrf(AbstractHttpConfigurer::disable)

                // Sessiya saqlanmaydi - har so'rov mustaqil
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 401 uchun custom javob
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthEntryPoint))

                // URL'lar bo'yicha ruxsat
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC - hammaga ochiq
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/confirm-email",
                                "/results/verify/**"        // ← QR orqali ko'rish - public

                        ).permitAll()

                        // Swagger ham public
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // ADMIN only
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Qolganlari - authenticated bo'lishi kerak
                        .anyRequest().authenticated()
                )

                // JWT filter qo'shish - UsernamePassword filter'dan oldin
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}