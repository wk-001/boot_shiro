package com.wk.jwt;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * JSON Web Token
 */
@Data
public class JWTToken implements AuthenticationToken {

    private static final long serialVersionUID = 1282057025599826155L;

    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}
