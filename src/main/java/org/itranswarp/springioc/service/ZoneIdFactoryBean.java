package org.itranswarp.springioc.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

//使用工厂模式创建Bean
@Component
public class ZoneIdFactoryBean implements FactoryBean<ZoneId> {
    String zone = "Z";
    @Override
    public ZoneId getObject() throws Exception {
        return ZoneId.of(zone);
    }

    @Override
    public Class<?> getObjectType() {
        return ZoneId.class;
    }
}
