package com.xlaser4j.opening.modules.sys.oauth2;

import java.util.Set;

import com.xlaser4j.opening.modules.sys.entity.SysUserDO;
import com.xlaser4j.opening.modules.sys.entity.SysUserTokenDO;
import com.xlaser4j.opening.modules.sys.service.IShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Oauth realm.
 *
 * @package: com.xlaser4j.opening.modules.sys.oauth2
 * @author: Elijah.D
 * @time: 2018/10/15 11:00
 * @description: shiro域, 认证授权
 * @modified: Elijah.D
 */
@Slf4j
@Component
public class OauthRealm extends AuthorizingRealm {
    private final IShiroService shiroService;

    /**
     * Instantiates a new Oauth realm.
     *
     * @param shiroService the shiro service
     */
    @Autowired
    public OauthRealm(IShiroService shiroService) {
        this.shiroService = shiroService;
    }

    /**
     * 是否自定义token
     *
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OauthToken;
    }

    /**
     * 授权(验证权限时调用)
     *
     * @param principal
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {

        // 获取用户权限
        SysUserDO user = (SysUserDO)principal.getPrimaryPrincipal();
        Long userId = user.getUserId();
        Set<String> permissions = shiroService.listPermissions(userId);

        //授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * 认证(登录时调用)
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 验证token
        String accessToken = (String)token.getPrincipal();
        SysUserTokenDO userToken = shiroService.getUserTokenByToken(accessToken);
        if (userToken == null || userToken.getExpireTime() < System.currentTimeMillis()) {
            log.error("【OauthRealm】认证失败! token失效,请重新登陆 !");
            throw new AuthenticationException("认证失败! token失效,请重新登陆 !");
        }

        // 根据token验证用户信息
        SysUserDO user = shiroService.getUserById(userToken.getUserId());
        if (user.getStatus() == 0) {
            log.error("【OauthRealm】认证失败! 账号已被锁定,请联系管理员 !");
            throw new LockedAccountException("认证失败! 账号已被锁定,请联系管理员 !");
        }

        // 认证成功
        return new SimpleAuthenticationInfo(user, accessToken, getName());
    }
}
