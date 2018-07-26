package com.hqs.springboot.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hqs.springboot.constants.CallContext;
import com.hqs.springboot.constants.Constants;
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
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Manu on 2018/7/17.
 */
@Component
public class RealmUtil extends Pac4jRealm {

    // 添加 @Component 注解，对象的生命周期将交由 Spring 接管 - 对象级别的 autowired 方可使用

    private final static Logger logger = LoggerFactory.getLogger(RealmUtil.class);

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysModuleMapper sysModuleMapper;
    @Value("${shiro.session-timeout}")
    private Integer sessionTimeout;

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
            session.setTimeout(sessionTimeout);

            if(session.getAttribute(Constants.CUR_USER) == null) {
                session.setAttribute(Constants.CUR_USER, currentUser);
            }

        }
        return info;
    }


    //清除缓存
    public void clearCached(List<String> userEmails){
        Subject subject = SecurityUtils.getSubject();
        String realmName = subject.getPrincipals().getRealmNames().iterator().next();
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        for (String userEmail: userEmails){
            logger.info("oper.user="+userEmail+",login.user="+SecurityUtils.getSubject().getPrincipal().toString());
            SimplePrincipalCollection principals = new SimplePrincipalCollection(userEmail,realmName);
            subject.runAs(principals);
            logger.info(subject.getPrincipal().toString());
            this.getAuthorizationCache().remove(subject.getPrincipals());
            this.getAuthorizationCache().remove(userEmail);
            subject.releaseRunAs();
        }
    }

}
