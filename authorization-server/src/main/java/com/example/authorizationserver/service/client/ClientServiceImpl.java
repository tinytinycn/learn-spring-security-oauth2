package com.example.authorizationserver.service.client;

import com.example.authorizationserver.constant.MessageConstant;
import com.example.authorizationserver.domain.entity.Client;
import com.example.authorizationserver.service.principal.ClientPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户端管理类
 * - @PostConstruct bean初始化时，准备一些客户端用户测试数据，实际生产可以连接DB查询客户端用户数据
 */
@Service
public class ClientServiceImpl implements ClientService {
    private List<Client> clientList;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initData() {
        String clientSecret = passwordEncoder.encode("123456");
        clientList = new ArrayList<>();
        // 1、密码模式
        clientList.add(Client.builder()
                .clientId("client-app")
                .resourceIds("oauth2-resource")
                .secretRequire(false)
                .clientSecret(clientSecret)
                .scopeRequire(false)
                .scope("all")
                .authorizedGrantTypes("password,refresh_token")
                .authorities("ADMIN,USER")
                .accessTokenValidity(3600)
                .refreshTokenValidity(86400).build());
        // 2、授权码模式
        clientList.add(Client.builder()
                .clientId("client-app-2")
                .resourceIds("oauth2-resource2")
                .secretRequire(false)
                .clientSecret(clientSecret)
                .scopeRequire(false)
                .scope("all")
                .authorizedGrantTypes("authorization_code,refresh_token") // 授权码客户
                .webServerRedirectUri("https://www.gathub.cn,https://www.baidu.com")
                .authorities("USER")
                .accessTokenValidity(3600)
                .refreshTokenValidity(86400).build());
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        // 通过客户clientId，进行用户存在校验
        List<Client> findClientList = clientList.stream()
                .filter(item -> item.getClientId().equals(clientId))
                .collect(Collectors.toList());
        if (findClientList.isEmpty()) {
            throw new NoSuchClientException(MessageConstant.NOT_FOUND_CLIENT);
        }
        return new ClientPrincipal(findClientList.get(0));
    }
}
