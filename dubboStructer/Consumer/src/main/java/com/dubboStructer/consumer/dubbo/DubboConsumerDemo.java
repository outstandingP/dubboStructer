package com.dubboStructer.consumer.dubbo;

import com.dubboStructer.service.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DubboConsumerDemo {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:application.xml"});
        ctx.start();

        // 获取远程服务代理
        HelloService helloservice = (HelloService) ctx.getBean("helloService");
        while (true) {
            try {
                System.out.println(helloservice.getName());
                Thread.currentThread().sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}