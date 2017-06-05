package com.outstanding.designPattern.AbstractFactory.factory;

import com.outstanding.designPattern.AbstractFactory.service.Provider;
import com.outstanding.designPattern.factoryMethod.service.Sender;
import com.outstanding.designPattern.factoryMethod.service.impl.MailSender;

/**
 * Created by songll on 2017/2/3.
 */
public class SendMailFactory implements Provider {
    @Override
    public Sender produce() {
        return new MailSender();
    }
}
