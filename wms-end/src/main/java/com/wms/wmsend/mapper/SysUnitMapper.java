package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.SysUnit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.UnitResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【sys_unit(单位信息表)】的数据库操作Mapper
* @createDate 2025-03-03 15:19:08
* @Entity com.wms.wmsend.model.entity.SysUnit
*/
public interface SysUnitMapper extends BaseMapper<SysUnit> {

    IPage<UnitResultVo> listByPage(@Param("page") Page<SysUnit> page,@Param("unitName") String unitName);
}




