package com.example.demo.util;

import com.example.demo.component.HttpRequestDetail;
import io.jsonwebtoken.lang.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class HttpUtil {
    /**
     * 通过跳转页进行重定向
     * @param response
     * @param indirectPageURL
     * @param xmlType
     * @param redirectCatchName
     * @param redirectDetail
     * @throws IOException
     */
    public static void redirectByPage(
            HttpServletResponse response,String indirectPageURL, String xmlType,
            String redirectCatchName, HttpRequestDetail redirectDetail) throws IOException {
        Assert.notNull(response, "重定向Response对象不能为空。");
        Assert.notNull(indirectPageURL, "间接跳转页面URL不能为空。");
        Assert.notNull(redirectDetail, "重定向URL不能为空。");

        StringBuilder requestURLBuilder = new StringBuilder();
        requestURLBuilder.append(indirectPageURL)
                .append("?").append(redirectCatchName).append("=")
                .append(redirectDetail.toString());

        //重定向
        if (!StringUtils.isEmpty(xmlType) && xmlType.toLowerCase().equals("xmlhttprequest")) {
            //是ajax请求,异步请求重定向(前端处理)
            response.setHeader("REDIRECT_LOCATION", requestURLBuilder.toString());//重定向目标地址
            response.setHeader("FLAG","1000");
        } else {
            //非ajax请求，直接使用重定向
            response.sendRedirect(requestURLBuilder.toString());
        }
    }

    /**
     * 重定向到页面
     * @param response
     * @param xmlType
     * @param pageURL
     * @param redirectCatch
     * @param redirectCatchName
     * @param signKey
     * @throws IOException
     */
    public static void redirectToPage(HttpServletResponse response, String xmlType, String pageURL, HttpRequestDetail redirectCatch, String redirectCatchName, String signKey) throws IOException {
        Assert.notNull(response, "重定向Response对象不能为空。");
        Assert.notNull(pageURL, "重定向RUL不能为空。");

        StringBuilder pageURLBuilder = new StringBuilder(pageURL);

        if (redirectCatch != null) {
            String parameterJson = redirectCatch.toString();
            pageURLBuilder.append("?").append(redirectCatchName).append("=");
            //如果有密钥，则加密参数，无密钥使用json字符串作为参数，最后将参数进行url编码
            if (StringUtils.isEmpty(signKey)) {
                pageURLBuilder.append(URLEncoder.encode(parameterJson, "UTF-8"));
            } else {
                pageURLBuilder.append(URLEncoder.encode(EncryptUtil.encrypt(parameterJson, signKey), "UTF-8"));
            }
        }

        if (!StringUtils.isEmpty(xmlType) && xmlType.toLowerCase().equals("xmlhttprequest")) {
            //是ajax请求,异步请求重定向(前端处理)
            response.setHeader("REDIRECT_LOCATION", pageURLBuilder.toString());//重定向目标地址
            response.setHeader("FLAG","1000");
        } else {
            //非ajax请求，直接使用重定向
            response.sendRedirect(pageURLBuilder.toString());
        }
    }
}
