package com.example.authorizationserver.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 加载用户特定数据的核心接口。
 * 它在整个框架中用作用户 DAO，是DaoAuthenticationProvider使用的策略。
 * 该接口只需要一种只读方法，这简化了对新数据访问策略的支持
 */
public interface UserService extends UserDetailsService {
}
