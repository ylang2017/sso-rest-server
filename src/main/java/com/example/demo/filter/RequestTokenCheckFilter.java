package com.example.demo.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.component.HttpRequestDetail;
import com.example.demo.config.SecurityProperties;
import com.example.demo.util.EncryptUtil;
import com.example.demo.util.HttpUtil;
import com.example.demo.util.JWTUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestTokenCheckFilter extends BasicAuthenticationFilter {
    private static final Logger logger = LogManager.getLogger(RequestTokenCheckFilter.class);
    private SecurityProperties securityProperties;

    public RequestTokenCheckFilter(AuthenticationManager authenticationManager, SecurityProperties securityProperties) {
        super(authenticationManager);
        this.securityProperties = securityProperties;
    }

    /**
     * 过滤用户请求是否携带有认证信息（token或cookie）。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("触发token验证,请求URL：{}", request.getRequestURL());
        logger.info("请求方法：{}", request.getMethod());
        logger.info("请求参数：{}", ((JSONObject) JSONObject.toJSON(request.getParameterMap())).toJSONString());


        String xRequestWith = request.getHeader("X-Requested-With");
        logger.info("X-Requested-With:{}",xRequestWith);


        String token = request.getHeader(securityProperties.getRequestHeaderKeyOfJWT());
        Boolean isHeaderTokenAvailable = false;
        if (!StringUtils.isEmpty(token)) {
            //调用sso rest api检查token有效性
            isHeaderTokenAvailable = true;
        }

        //如果header中携带的token无效，则使用cookie中的token
        Boolean isCookieTokenAvailable = false;
        if (!isHeaderTokenAvailable) {
            token = analyzeRequestCookie(request);
            if (!StringUtils.isEmpty(token)) {
                //调用sso rest api检查token有效性
                isCookieTokenAvailable = true;
            }
        }

        //cookie和header中都没有有效token,调用sso服务校验当前用户
        if (!isHeaderTokenAvailable && !isHeaderTokenAvailable) {
            logger.info("token验证结束：header和cookie均未携带有效token");
            //重定向到sso服务，判断是否在认证中心有授权
            HttpRequestDetail httpRedirectDetail = new HttpRequestDetail();
            httpRedirectDetail.setRequestMethod(request.getMethod());
            httpRedirectDetail.setRequestURL(request.getRequestURL().toString());
            httpRedirectDetail.setRequestParams(new HashMap<>());
            Map<String,String[]> oriParams = request.getParameterMap();
            if(oriParams!=null && !oriParams.isEmpty()){
                Map<String,Object> params = httpRedirectDetail.getRequestParams();
                oriParams.forEach((key,arr)->{
                    if(arr.length>=2){
                        params.put(key,arr);
                    }else{
                        params.put(key,arr[0]);
                    }
                });
            }

            HttpRequestDetail ssoRedirectDetail = new HttpRequestDetail();
            ssoRedirectDetail.setRequestMethod("post");
            ssoRedirectDetail.setRequestURL(securityProperties.getSecurityCheckUserApiURL());
            Map<String,Object> requestParams = new HashMap<>();
            requestParams.put("httpRedirectDetail",httpRedirectDetail);
            ssoRedirectDetail.setRequestParams(requestParams);
            logger.info("重定向：{}",ssoRedirectDetail.toString());

            HttpUtil.redirectToPage(response,xRequestWith,securityProperties.getSecurityRedirectPage(),ssoRedirectDetail,
                    securityProperties.getRequestEncryptParameterName(),null);
            return;
        }

        //jwt解密
        String decryptToken = EncryptUtil.decrypt(token, securityProperties.getTokenSignKey());
        logger.info("token验证结束：解密后的token:{}", decryptToken);

        // 解析jwt
        UsernamePasswordAuthenticationToken authentication =
                JWTUtil.parseJWTtoAuthenticationToken(
                        decryptToken, securityProperties.getSchemeOfJWT(), securityProperties.getSigningKeyOfJWT());

        // 防止jwt过期，其实这里应该统一策略，使用缓存层管理token过期时间及状态。避免重复判断
        if (authentication == null) {
            logger.info("token验证结束：header携带了无效的token");
            //重定向todo

            return;
        }

        logger.info("token验证结束：携带了有效的token，允许访问");
        //将携带的认证和授权信息写入SecurityContextHolder中供后续使用
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    /**
     * 从cookie中解析出token
     *
     * @param request
     * @return
     */
    private String analyzeRequestCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            Optional<Cookie> authCookies = Arrays.stream(cookies)
                    .filter(cookie -> securityProperties.getRequestCookieKeyOfJWT().equals(cookie.getName()))
                    .findFirst();
            if(authCookies.isPresent()){
                Cookie authCookie = authCookies.get();
                String token = authCookie.getValue();
                if (StringUtils.isEmpty(token)) {
                    //清除无效cookie
                    authCookie.setMaxAge(0);
                }
                return token;
            }
        }
        return null;
    }
}
