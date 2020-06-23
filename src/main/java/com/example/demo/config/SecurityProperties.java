package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 安全认证配置属性
 */
@Component
public class SecurityProperties {
    /**
     * JWT秘钥
     */
    @Value("${JWT.signing.key}")
    @Getter
    private String signingKeyOfJWT;

    /**
     * JWT中保存授权（角色）的key
     */
    @Value("${JWT.role.key}")
    @Getter
    private String roleKeyInToken;

    /**
     * JWT认证方案
     */
    @Value("${JWT.scheme}")
    private String schemeOfJWT;

    /**
     * 请求头中携带的jwt名称
     */
    @Value("${JWT.requestHeader.name}")
    @Getter
    private String requestHeaderKeyOfJWT;

    /**
     * cookie中携带的jwt名称
     */
    @Value("${JWT.cookie.name}")
    @Getter
    private String requestCookieKeyOfJWT;

    /**
     * 自定义登陆表单中的用户名
     */
    @Value("${security.loginForm.username}")
    @Getter
    private String loginFormUserName;

    /**
     * 自定义登陆表单中的密码
     */
    @Value("${security.loginForm.pass}")
    @Getter
    private String loginFormPassword;

    /**
     * JWT存活时间
     */
    @Value("${JWT.survival.time}")
    @Getter
    private Long survivalTimeOfJWT;

    /**
     * 保存JWT的cookie存活时间
     */
    @Value("${JWT.cookie.survival.time}")
    @Getter
    private Long survivalTimeOfJWTCookie;

    /**
     * 对返回客户端的jwt进行二次加密时使用的秘钥
     */
    @Value("${token.sign.key}")
    @Getter
    private String tokenSignKey;

    /**
     * 请求参数加密密钥
     */
    @Value("${request.parameter.sign.key}")
    @Getter
    private String requestParameterSignKey;

    /**
     * 被加密的参数名称
     */
    @Value("${request.encrypt.parameter.name}")
    @Getter
    private String requestEncryptParameterName;

    /**
     * 自定义登录页
     */
    @Value("${security.login.page}")
    @Getter
    private String securityLoginPage;

    /**
     * 自定义重定向跳转页
     */
    @Value("${security.redirect.page}")
    @Getter
    private String securityRedirectPage;

    /**
     * 用户身份认证api URL
     */
    @Value("${security.checkUser.api.url}")
    @Getter
    private String securityCheckUserApiURL;

    public String getSchemeOfJWT() {
        return this.schemeOfJWT + " ";
    }
}
