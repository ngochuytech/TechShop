package com.project.techstore.configurations;

import com.project.techstore.filter.JwtAuthFilter;
import com.project.techstore.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig  {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.addAllowedHeader("*");
                    config.setAllowCredentials(true);
                    return config;
                }))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/verify/**", apiPrefix),
                                String.format("%s/users/auth/social-login", apiPrefix),
                                String.format("%s/users/auth/social/callback", apiPrefix)
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, String.format("%s/notifications/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.PUT, String.format("%s/notifications/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, String.format("%s/notifications/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)

                        .requestMatchers(HttpMethod.GET, String.format("%s/orders/status",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.POST, String.format("%s/orders/**",apiPrefix)).hasAnyRole(Role.USER, Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.PUT, String.format("%s/orders/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)

                        .requestMatchers(HttpMethod.POST,String.format("%s/products/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.PUT,String.format("%s/products/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.DELETE,String.format("%s/products/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)

                        .requestMatchers(HttpMethod.POST, String.format("%s/product-models/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.PUT, String.format("%s/product-models/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, String.format("%s/product-models/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)

                        .requestMatchers(HttpMethod.POST, String.format("%s/promotions/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.PUT, String.format("%s/promotions/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, String.format("%s/promotions/**",apiPrefix)).hasAnyRole(Role.STAFF, Role.ADMIN)

                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
