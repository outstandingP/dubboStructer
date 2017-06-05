package com.outstanding.designPattern.factoryMethod.service.impl;

import com.outstanding.designPattern.factoryMethod.service.Sender;

/**
 * Created by songll on 2017/2/3.
 */
public class SmsSender implements Sender {
    @Override
    public void Send() {
        System.out.println("this is sms sender!");
    }
}
