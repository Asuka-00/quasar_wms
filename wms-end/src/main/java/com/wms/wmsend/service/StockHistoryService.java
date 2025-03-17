package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.StockHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.StockHistoryResultVo;

/**
* @author Ayanami
* @description 针对表【stock_history】的数据库操作Service
* @createDate 2025-03-05 20:14:54
*/
public interface StockHistoryService extends IService<StockHistory> {

    void saveHistory(Long warehouseId, Long productId, Long diff, String actionType,String remark);

    IPage<StockHistoryResultVo> listByPage(Page<StockHistory> page, String warehouseId, String productId, String actionName,String docNo);
}
