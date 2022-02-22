package com.example.gatewayserver.config;

import com.example.gatewayserver.component.CustomizedAuthorizationManager;
import com.example.gatewayserver.component.RestAuthenticationEntryPoint;
import com.example.gatewayserver.component.RestfulAccessDeniedHandler;
import com.example.gatewayserver.constant.AuthConstant;
import com.example.gatewayserver.filter.IgnoreUrlsRemoveJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * webflux安全配置
 */
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
    private final CustomizedAuthorizationManager customizedAuthorizationManager; // 鉴权管理器配置
    private final IgnoreUrlsConfig ignoreUrlsConfig; // 白名单配置
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler; // 自定义未授权的处理结果
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint; // 自定义没有认证或token过期时的返回结果
    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter; // 对白名单路径，直接移除JWT请求头

    public WebSecurityConfig(CustomizedAuthorizationManager customizedAuthorizationManager, IgnoreUrlsConfig ignoreUrlsConfig, RestfulAccessDeniedHandler restfulAccessDeniedHandler, RestAuthenticationEntryPoint restAuthenticationEntryPoint, IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter) {
        this.customizedAuthorizationManager = customizedAuthorizationManager;
        this.ignoreUrlsConfig = ignoreUrlsConfig;
        this.restfulAccessDeniedHandler = restfulAccessDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.ignoreUrlsRemoveJwtFilter = ignoreUrlsRemoveJwtFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer()
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
                .and()
                .authenticationEntryPoint(restAuthenticationEntryPoint);
        http.addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
                .pathMatchers(ignoreUrlsConfig.getUrls().toArray(new String[]{})).permitAll()
                .anyExchange().access(customizedAuthorizationManager)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .csrf().disable();
        return http.build();
    }

    /**
     * JWT认证转换器
     */
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
