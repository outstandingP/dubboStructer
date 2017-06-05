package com.outstanding.designPattern.AbstractFactory.service;

import com.outstanding.designPattern.factoryMethod.service.Sender;

/**
 * Created by songll on 2017/2/3.
 */
public interface Provider {
    public Sender produce();
}
