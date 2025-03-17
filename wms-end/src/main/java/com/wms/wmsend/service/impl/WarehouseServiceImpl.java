package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.Warehouse;
import com.wms.wmsend.model.vo.WarehouseResultVo;
import com.wms.wmsend.service.WarehouseService;
import com.wms.wmsend.mapper.WarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【warehouse(仓库信息表)】的数据库操作Service实现
* @createDate 2025-03-03 16:07:56
*/
@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse>
    implements WarehouseService{

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public IPage<WarehouseResultVo> listByPage(Page<Warehouse> page, String warehouseName, String unitId) {
        return warehouseMapper.listByPage(page,warehouseName,unitId);
    }
}




