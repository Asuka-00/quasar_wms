package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.StockAlert;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.StockAlertResultVo;

/**
* @author Ayanami
* @description 针对表【stock_alert(库存预警表)】的数据库操作Service
* @createDate 2025-03-04 12:48:46
*/
public interface StockAlertService extends IService<StockAlert> {

    IPage<StockAlertResultVo> listByPage(Page<StockAlert> page, String productId, String warehouseId);
}
