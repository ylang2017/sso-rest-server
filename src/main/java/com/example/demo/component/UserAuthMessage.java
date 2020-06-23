package com.example.demo.component;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserAuthMessage implements UserDetails {
    //默认的用户及权限信息
    private String username;
    private String password;
    private Set<GrantedAuthority> authorities;
    //可以扩展别的认证信息，比如授权过期日期等

    public UserAuthMessage() {
    }

    public UserAuthMessage(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = new HashSet<>(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{")
                .append("\"username\"" + ":\"").append(username).append("\",");

        stringBuilder.append("\"authorities\"" + ":[");
        if (authorities != null && !authorities.isEmpty()) {
            authorities.forEach((grantedAuthority) ->
                    stringBuilder.append("\"").append(grantedAuthority.getAuthority()).append("\","));
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        }
        stringBuilder.append("]");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
