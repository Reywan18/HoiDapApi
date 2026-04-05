package com.hoidap.hoidapdemo.config;

import com.hoidap.hoidapdemo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Bean mã hóa mật khẩu.
     * Sử dụng BCrypt để mã hóa một chiều. Đây là chuẩn bảo mật hiện nay.
     * Khi user đăng nhập, mật khẩu nhập vào sẽ được mã hóa và so sánh với chuỗi
     * trong DB.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean quản lý xác thực (AuthenticationManager).
     * Được sử dụng trong AuthService để thực hiện lệnh .authenticate() (kiểm tra
     * username/password).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Cấu hình cốt lõi của Spring Security (SecurityFilterChain).
     * Tại đây định nghĩa:
     * 1. CORS: Cho phép Frontend gọi API.
     * 2. CSRF: Tắt (vì dùng JWT stateless nên không cần chống CSRF token).
     * 3. AuthorizeHttpRequests: Phân quyền ai được truy cập đường dẫn nào.
     * 4. FormLogin: Cấu hình trang đăng nhập cho Admin.
     * 5. AddFilterBefore: Chèn bộ lọc JWT vào trước bộ lọc mặc định để check Token.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // --- PHÂN QUYỀN TRUY CẬP (AUTHORIZATION) ---
                .authorizeHttpRequests(auth -> auth
                        // Tài liệu API (Swagger) và tài nguyên tĩnh (css, js)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**").permitAll()
                        // Dành riêng cho ADMIN
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                        .requestMatchers("/api/reports/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                        // API Chung
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/setup/**", "/api/classes/**")
                        .permitAll()
                        // WebSockets Endpoint
                        .requestMatchers("/ws/**").permitAll()
                        // API Question
                        .requestMatchers(HttpMethod.POST, "/api/questions/*/answer").hasRole("CVHT")
                        .requestMatchers("/api/questions/advisor/**").hasRole("CVHT")
                        .requestMatchers("/api/questions/*/file").permitAll()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/questions/**").hasRole("SINH_VIEN")
                        .requestMatchers(HttpMethod.PUT, "/api/questions/**").hasRole("SINH_VIEN")
                        .requestMatchers("/api/questions/**").hasAnyRole("SINH_VIEN", "CVHT", "ADMIN")

                        .anyRequest().authenticated())
                // Form login cho trang Admin
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll())
                .logout(logout -> logout.permitAll());

        // Thêm bộ lọc JWT vào trước bộ lọc xác thực username/password mặc định
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cấu hình CORS (Cross-Origin Resource Sharing).
     * Cho phép các tên miền (Frontend) cụ thể được phép gọi vào API của Backend.
     * Nếu không có cái này, trình duyệt sẽ chặn request từ React (port 3000) gọi
     * sang Spring Boot (port 8080).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép Frontend React chạy ở localhost:3000 và 5173
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Cho phép tất cả các phương thức (GET, POST, PUT, DELETE...)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép mọi Header
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi kèm thông tin xác thực (Cookie, Authorization Header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho toàn bộ API
        return source;
    }
}
