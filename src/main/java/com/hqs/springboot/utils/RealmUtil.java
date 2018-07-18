package com.hqs.springboot.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hqs.springboot.constants.CallContext;
import com.hqs.springboot.entity.SysModule;
import com.hqs.springboot.entity.SysUser;
import com.hqs.springboot.mapper.SysModuleMapper;
import com.hqs.springboot.mapper.SysUserMapper;
import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Manu on 2018/7/17.
 */
@Component
public class RealmUtil extends Pac4jRealm {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysModuleMapper sysModuleMapper;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final Pac4jToken token = (Pac4jToken) authenticationToken;
        final LinkedHashMap<String, CommonProfile> profiles = token.getProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);

        String email = principal.getProfile().getId();

        if(!email.contains("@xiaomi.com")){
            email += "@xiaomi.com";
        }

        SysUser sysUser = sysUserMapper.selectByEmail(email);

        if(sysUser == null){
            this.getAuthorizationCache().remove("guest");

            return new SimpleAuthenticationInfo("guest",profiles.hashCode(),getName());
        }else{
            //防止用户强制清除cookie，遗留的redis缓存
            this.getAuthorizationCache().remove(sysUser.getEmail());

            return new SimpleAuthenticationInfo(sysUser.getEmail(),profiles.hashCode(),getName());
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String email = (String) super.getAvailablePrincipal(principalCollection);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        if(email.equals("guest")){
            throw new UnauthorizedException("认证失败");
        }else{
            //实时查询用户拥有的角色和权限
            SysUser currentUser = sysUserMapper.selectByEmail(email);

            if(currentUser == null) {
                throw new UnauthorizedException("用户不存在");
            }

            //TODO：读取user权限
            // List<SysModule> moduleDTOList = this.sysModuleMapper.searchByUserId(currentUser.getId());
            List<SysModule> moduleDTOList = this.sysModuleMapper.searchById(new Long(2));

            List<String> permissions = Lists.transform(moduleDTOList, new Function<SysModule, String>() {
                @Override
                public String apply(SysModule sysModule) {
                    return sysModule.getCodeName();
                }
            });

            //将权限放入shiro中
            info.addStringPermissions(permissions);

            Session session = SecurityUtils.getSubject().getSession();
            session.setTimeout(-1000l);

            if(session.getAttribute(CallContext.CUR_USER) == null) {
                session.setAttribute(CallContext.CUR_USER, currentUser);
            }

        }

        return info;
    }

}
