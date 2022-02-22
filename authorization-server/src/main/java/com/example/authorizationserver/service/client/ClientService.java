package com.example.authorizationserver.service.client;

import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * 加载客户端特定数据
 * 通过客户端 ID 加载客户端。此方法不得返回 null。
 */
public interface ClientService extends ClientDetailsService {
}
