package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.component.UserAuthMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JWTUtil {
    /**
     * 通过user生成jwt
     *
     * @param user
     * @param survivalTime
     * @param signingKey
     * @return
     */
    public static String generateJWTByUser(UserAuthMessage user, Long survivalTime, String signingKey) {
        //构建一个JWT
        return Jwts.builder()
                //jwt的主体
                .setSubject(user.toString())

                //自定义的属性
                //.claim(securityProperties.getRoleKeyInToken(), auth.getAuthorities())

                //设置token过期时间
                .setExpiration(new Date(System.currentTimeMillis() + survivalTime))

                //设置token签名加密方式、密钥
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }

    /**
     * 将jwt解析为user对象
     *
     * @param jwt
     * @param jwtScheme
     * @param signingKey
     * @return
     */
    public static UserAuthMessage parseJWTtoUser(String jwt, String jwtScheme, String signingKey) {
        String token = jwt.startsWith(jwtScheme) ? jwt.replace(jwtScheme, "") : jwt;

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)//设置解密密钥
                    .parseClaimsJws(token)
                    .getBody();

            String sub = claims.getSubject();

            if (StringUtils.isEmpty(sub)) {
                return null;
            }

            JSONObject subJson = JSON.parseObject(sub);
            String username = subJson.getString("username");

            if (StringUtils.isEmpty(username)) {
                return null;
            }

            JSONArray arr = subJson.getJSONArray("authorities");
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (arr != null && !arr.isEmpty()) {
                arr.forEach((auth) -> {
                    authorities.add(new SimpleGrantedAuthority(String.valueOf(auth)));
                });
            }

            return new UserAuthMessage(username, null, authorities);
        } catch (ExpiredJwtException e) {
            //jwt过期
            System.out.println("jwt已过期");
            return null;
        }
    }

    public static String generateJWTByAuthenticationToken(
            Authentication token, Long survivalTime, String signingKey) {
        //构建一个JWT
        return Jwts.builder()
                //jwt的主体
                .setSubject(String.valueOf(token.getPrincipal()))

                //自定义的属性
                //.claim(securityProperties.getRoleKeyInToken(), auth.getAuthorities())

                //设置token过期时间
                .setExpiration(new Date(System.currentTimeMillis() + survivalTime))

                //设置token签名加密方式、密钥
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }

    /**
     * 将jwt解析为供内部传递的AuthenticationToken
     *
     * @param jwt
     * @param jwtScheme
     * @param signingKey
     * @return
     */
    public static UsernamePasswordAuthenticationToken parseJWTtoAuthenticationToken(
            String jwt, String jwtScheme, String signingKey) {
        UserAuthMessage user = parseJWTtoUser(jwt, jwtScheme, signingKey);

        if (user == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(user.toString(), null, user.getAuthorities());
    }

}
