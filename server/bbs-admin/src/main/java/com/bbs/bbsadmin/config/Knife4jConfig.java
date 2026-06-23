package com.bbs.bbsadmin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    /**
     * 地址：http://127.0.0.1:8888/doc.html
     *
     */
    @Bean
    public GroupedOpenApi adminApi() {      // 创建了一个api接口的分组
        return GroupedOpenApi.builder()
                .group("admin-api")         // 分组名称
                .pathsToMatch("/admin/**")  // 接口请求路径规则
                .build();
    }
    /***
     * @description 自定义接口信息 + 注册 Bearer Token 鉴权
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 全局生效:除 @Operation 显式声明 security = {} 外,默认都要传 token
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");
        return new OpenAPI()
                .info(new Info()
                        .title("API接口文档")
                        .version("1.0")
                        .description("API接口文档")
                        .contact(new Contact().name("yuni")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("请在登录接口获取 token 后,粘贴到此处")))
                .addSecurityItem(securityRequirement);
    }
}
