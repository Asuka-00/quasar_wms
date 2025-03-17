package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.SysUnit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.UnitResultVo;

/**
* @author Ayanami
* @description 针对表【sys_unit(单位信息表)】的数据库操作Service
* @createDate 2025-03-03 15:19:08
*/
public interface SysUnitService extends IService<SysUnit> {

    IPage<UnitResultVo> listByPage(Page<SysUnit> page, String unitName);
}
