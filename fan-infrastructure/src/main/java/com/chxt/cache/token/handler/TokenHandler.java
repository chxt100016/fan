package com.chxt.cache.token.handler;

import com.chxt.cache.token.model.TokenHandlerParam;
import com.chxt.cache.token.model.TokenItem;

public interface TokenHandler {
    TokenItem getToken(TokenHandlerParam param);

    TokenItem refreshToken(TokenHandlerParam param, TokenItem tokenItem);

}
