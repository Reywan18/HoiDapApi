package com.hoidap.hoidapdemo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /**
     * Cấu hình chính cho Swagger/OpenAPI.
     * 1. Thiết lập Tiêu đề, Phiên bản, Mô tả cho trang tài liệu.
     * 2. Cấu hình nút "Authorize" (ổ khóa) để test API cần đăng nhập.
     * Sử dụng cơ chế Bearer Token (JWT).
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HoiDap Demo API")
                        .version("1.0")
                        .description("Tài liệu API cho hệ thống Hỏi đáp Sinh viên - CVHT"))

                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}