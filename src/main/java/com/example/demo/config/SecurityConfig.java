package com.example.demo.config;

import com.example.demo.filter.RequestTokenCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭跨站请求防护(后面要考虑csrf防护)
                .cors().and().csrf().disable()
                .authorizeRequests()
                //不需要通过登录验证就可以被访问的资源路径
                //.antMatchers("/test", "/login.html", "/ossLogin", "/loginProcessor", "*.ico").permitAll()
                //需要角色权限访问
                //.antMatchers("/admin").hasAnyAuthority("ROLE_ADMIN")  //前面是资源的访问路径、后面是资源的名称或者叫资源ID
                //.antMatchers("/user").hasAnyAuthority("ROLE_USER")
                //其它所有资源都需要授权后访问
                .anyRequest().authenticated()
                .and()
                //增加身份验证过滤
                .addFilter(new RequestTokenCheckFilter(authenticationManager(), securityProperties))

                //前后端分离是无状态的，所以不用session，將登陆信息保存在token中。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    //配置跨域访问资源
    /*@Bean
    public CorsConfigurationSource CorsConfigurationSource() {
        CorsConfigurationSource source =   new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");	//同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
        corsConfiguration.addAllowedHeader("*");//header，允许哪些header，比如我们要用jwt Authorization则可以设置为Authorization
        corsConfiguration.addAllowedMethod("*");	//允许的请求方法，PSOT、GET等
        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**",corsConfiguration); //配置允许跨域访问的url
        return source;
    }*/
}
