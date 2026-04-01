package com.chxt.cache.token;


import com.chxt.cache.token.model.TokenItem;
import com.chxt.cache.token.tokenStore.InnerTokenStore;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.springframework.stereotype.Component;

@Component
public class TokenFactory{
    public static ApplicationContext context;

    @Autowired
    public TokenFactory( ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static TokenItem innerStore(TokenEnum tokenEnum) {
        return context.getBean(InnerTokenStore.class).getToken(tokenEnum);
    }


}
