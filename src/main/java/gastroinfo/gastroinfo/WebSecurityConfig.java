package gastroinfo.gastroinfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/**", "/yummy-town-admin/**")
                        .hasAuthority("ROLE_PLACE")
                        .requestMatchers("/rankings/*/edit")
                        .hasAuthority("ROLE_USER")
                        .anyRequest().permitAll()
                )
                .formLogin()
                .permitAll()
                .and()
                .logout()
                .and()
                .csrf().ignoringRequestMatchers("/api/**");
        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // We need to support plain passwords for now
        return NoOpPasswordEncoder.getInstance();
    }
}


