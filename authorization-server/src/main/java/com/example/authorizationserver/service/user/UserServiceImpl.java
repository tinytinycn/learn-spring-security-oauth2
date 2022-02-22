package com.example.authorizationserver.service.user;

import com.example.authorizationserver.constant.MessageConstant;
import com.example.authorizationserver.domain.entity.User;
import com.example.authorizationserver.service.principal.UserPrincipal;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理类
 * - @postConstruct bean初始化时，准备一些用户测试数据，实际生产可以连接DB查询用户数据
 */
@Service
public class UserServiceImpl implements UserService {

    // 缓存用户数据，用于测试
    private List<User> userList;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // 缓存用户数据，用于测试
    @PostConstruct
    public void initData() {
        String password = passwordEncoder.encode("123456");
        userList = new ArrayList<>();
        userList.add(new User(1L, "admin", password, 1, Arrays.asList("ADMIN")));
        userList.add(new User(2L, "user", password, 1, Arrays.asList("USER")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户user，进行用户存在校验
        List<User> findUserList = userList.stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
        if (findUserList.isEmpty()) {
            // 查无此用户
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        // 构建用户登陆信息userDetail，进行进一步的登陆信息校验
        UserPrincipal userPrincipal = new UserPrincipal(findUserList.get(0));
        if (!userPrincipal.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!userPrincipal.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!userPrincipal.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!userPrincipal.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
        return userPrincipal;
    }

}
