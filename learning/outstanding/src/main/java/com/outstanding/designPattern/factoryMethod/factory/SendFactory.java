package com.outstanding.designPattern.factoryMethod.factory;

import com.outstanding.designPattern.factoryMethod.service.Sender;
import com.outstanding.designPattern.factoryMethod.service.impl.MailSender;
import com.outstanding.designPattern.factoryMethod.service.impl.SmsSender;

/**
 * 总体来说，工厂模式适合：
 * 凡是出现了大量的产品需要创建，并且具有共同的接口时，可以通过工厂方法模式进行创建。
 * 在以上的三种模式中，第一种如果传入的字符串有误，不能正确创建对象，
 * 第三种相对于第二种，不需要实例化工厂类，
 * 所以，大多数情况下，我们会选用第三种——静态工厂方法模式。
 * Created by songll on 2017/2/3.
 */
public class SendFactory {

    /**
     * 普通工厂模式
     *
     * @param type
     * @return
     */
    public Sender produce(String type) {
        if ("mail".equals(type)) {
            return new MailSender();
        } else if ("sms".equals(type)) {
            return new SmsSender();
        } else {
            System.out.println("请输入正确的类型!");
            return null;
        }
    }

    /**
     * 以下是：多个工厂方法模式
     */
    public Sender produceMail() {
        return new MailSender();
    }

    public Sender produceSms() {
        return new SmsSender();
    }

    /**
     * 以下是：静态工厂方法模式
     * @return
     */
    public static Sender produceMail1(){
        return new MailSender();
    }

    public static Sender produceSms1(){
        return new SmsSender();
    }
}
