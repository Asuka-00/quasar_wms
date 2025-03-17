package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.UserQueryVo;
import com.wms.wmsend.model.vo.UserResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【sys_user(用户信息表)】的数据库操作Mapper
* @createDate 2025-03-03 14:28:19
* @Entity com.wms.wmsend.model.entity.SysUser
*/
public interface SysUserMapper extends BaseMapper<SysUser> {

    IPage<UserResultVo> listByPage(@Param("page") Page<SysUser> page, @Param("queryVo") UserQueryVo queryVo);
}




