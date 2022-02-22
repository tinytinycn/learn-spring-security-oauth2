package com.example.authorizationserver.component;

import com.example.authorizationserver.service.principal.UserPrincipal;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT内容增强
 * - 扩展jwt的负载信息，例如新增字段
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // 在认证信息中获取用户登陆信息userDetail
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // 将用户登录信息userDetail中的部分字段，放置在jwt中，扩展jwt内容
        // 把用户ID设置到JWT中
        Map<String, Object> info = new HashMap<>();
        info.put("id", userPrincipal.getId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
