package ma.enset.patienttest.Config;

import lombok.RequiredArgsConstructor;
import ma.enset.patienttest.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI public
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Ressources statiques et login public
                        .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**").permitAll()
                        // API REST notifications : accessible à tous les authentifiés
                        .requestMatchers("/api/notifications/**").authenticated()
                        // API REST : réservée aux ADMIN uniquement
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        // Actions d'administration
                        .requestMatchers("/admin/**", "/deletePatient", "/savePatient", "/editPatient/**").hasRole("ADMIN")
                        // Pages accessibles à ADMIN et USER
                        .requestMatchers("/index", "/patient/**", "/consultations/**", "/rendez-vous/**", "/dashboard", "/notifications/**").hasAnyRole("ADMIN", "USER")
                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/index", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}
