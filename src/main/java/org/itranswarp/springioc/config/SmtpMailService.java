package org.itranswarp.springioc.config;

import org.itranswarp.springioc.service.MailService;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;


@Component
@Conditional(OnSmtpEnvCondition.class) //按条件装配注入 实现Condition接口
public class SmtpMailService extends MailService {
}

class OnSmtpEnvCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return "true".equalsIgnoreCase(System.getenv("smtp"));
    }
}