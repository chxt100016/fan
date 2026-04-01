package com.chxt.cache.token.tokenStore;

import com.chxt.cache.token.TokenEnum;
import com.chxt.cache.token.model.TokenItem;

public interface TokenStore {


    TokenItem getToken(TokenEnum tokenEnum);
}
