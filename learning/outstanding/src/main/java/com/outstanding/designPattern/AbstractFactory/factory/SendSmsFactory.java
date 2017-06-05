package com.outstanding.designPattern.AbstractFactory.factory;

import com.outstanding.designPattern.AbstractFactory.service.Provider;
import com.outstanding.designPattern.factoryMethod.service.Sender;
import com.outstanding.designPattern.factoryMethod.service.impl.SmsSender;

/**
 * Created by songll on 2017/2/3.
 */
public class SendSmsFactory implements Provider {
    @Override
    public Sender produce() {
        return new SmsSender();
    }
}