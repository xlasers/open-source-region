package com.xlaser4j.opening.modules.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xlaser4j.opening.modules.sys.entity.SysUserDO;

/**
 * <p>
 * service: 用户服务
 * </p>
 *
 * @package: com.xlaser4j.opening.modules.sys.service
 * @author: Elijah.D
 * @time: 2018/10/11 19:40
 * @description: 服务类, 用户
 * @modified: Elijah.D
 */
public interface ISysUserService extends IService<SysUserDO> {
    /**
     * <p> 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return user 实体
     */
    SysUserDO getUserByName(String username);

    /**
     * <p> 获取用户权限id集合
     *
     * @param id 用户id
     * @return list 权限ids
     */
    List<Long> listMenuIdsByUserId(Long id);
}
