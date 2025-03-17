package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.Warehouse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.WarehouseResultVo;

/**
* @author Ayanami
* @description 针对表【warehouse(仓库信息表)】的数据库操作Service
* @createDate 2025-03-03 16:07:56
*/
public interface WarehouseService extends IService<Warehouse> {

    IPage<WarehouseResultVo> listByPage(Page<Warehouse> page, String wareHouseName, String unitId);
}
