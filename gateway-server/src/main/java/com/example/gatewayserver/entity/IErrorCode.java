package com.example.gatewayserver.entity;


/**
 * 封装API的错误码
 *
 * @author Honghui [wanghonghui_work@163.com] 2021/3/16
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}