package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.*;

/**
* @author Ayanami
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2025-03-03 14:28:19
*/
public interface SysUserService extends IService<SysUser> {

    IPage<UserResultVo> listByPage(Page<SysUser> page, UserQueryVo queryVo);

    CaptchaVo getCaptcha();

    LoginResultVo login(LoginVo loginVo);

    void register(RegisterVo registerVo);
}
