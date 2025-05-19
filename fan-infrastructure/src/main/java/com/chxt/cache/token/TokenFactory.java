package com.chxt.cache.token;


import com.chxt.cache.token.model.TokenItem;
import com.chxt.cache.token.tokenStore.InnerTokenStore;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class TokenFactory implements ApplicationContextAware {
    public static ApplicationContext context;

    @Override
    public void setApplicationContext( ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static TokenItem innerStore(TokenEnum tokenEnum) {
        return context.getBean(InnerTokenStore.class).getToken(tokenEnum);
    }


}
