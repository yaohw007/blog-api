package cn.poile.blog.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author: yaohw
 * @create: 2019-10-24 16:45
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomUserDetails extends UserVo implements UserDetails {

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!CollectionUtils.isEmpty(roles)) {
            return roles.stream().map(this::createAuthority).collect(Collectors.toSet());
        }
        return Collections.emptyList();
    }

    private GrantedAuthority createAuthority(String authority) {
       return (()->authority);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return !getStatus().equals(3);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !getStatus().equals(1);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !getStatus().equals(2);
    }
}
