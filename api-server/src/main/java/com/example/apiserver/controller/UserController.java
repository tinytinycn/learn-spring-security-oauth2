package com.example.apiserver.controller;

import com.example.apiserver.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;

@RestController
@RequestMapping("/user")
public class UserController {


    @SneakyThrows
    @PostMapping("/currentUser")
    public User currentUser(@RequestBody User user) {
        System.out.println(user.toString());
        return user;
    }
}
