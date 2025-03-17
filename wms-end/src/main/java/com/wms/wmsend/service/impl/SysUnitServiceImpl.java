package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.SysUnit;
import com.wms.wmsend.model.vo.UnitResultVo;
import com.wms.wmsend.service.SysUnitService;
import com.wms.wmsend.mapper.SysUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【sys_unit(单位信息表)】的数据库操作Service实现
* @createDate 2025-03-03 15:19:08
*/
@Service
public class SysUnitServiceImpl extends ServiceImpl<SysUnitMapper, SysUnit>
    implements SysUnitService{

    @Autowired
    private SysUnitMapper sysUnitMapper;

    @Override
    public IPage<UnitResultVo> listByPage(Page<SysUnit> page, String unitName) {
        return sysUnitMapper.listByPage(page, unitName);
    }
}




