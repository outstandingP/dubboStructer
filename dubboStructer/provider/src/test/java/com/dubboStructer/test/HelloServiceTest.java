package com.dubboStructer.test;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class HelloServiceTest {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"application.xml"});
        ctx.start();
        System.out.println("服务提供者已注册成功！");
        System.in.read();
    }
}