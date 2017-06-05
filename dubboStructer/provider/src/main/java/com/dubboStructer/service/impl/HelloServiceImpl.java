package com.dubboStructer.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dubboStructer.service.HelloService;
import org.springframework.stereotype.Component;

/**
 * Created by songll on 2017/5/2.
 */
@Component("helloService")
@Service
public class HelloServiceImpl implements HelloService {
    public String getName() {
        return "test dubbo provider success";
    }
}
