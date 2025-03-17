package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.StockHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.StockHistoryResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【stock_history】的数据库操作Mapper
* @createDate 2025-03-05 20:14:54
* @Entity com.wms.wmsend.model.entity.StockHistory
*/
public interface StockHistoryMapper extends BaseMapper<StockHistory> {

    IPage<StockHistoryResultVo> listByPage(@Param("page") Page<StockHistory> page,@Param("warehouseId") String warehouseId,
                                           @Param("productId") String productId, @Param("actionName") String actionName,@Param("docNo") String docNo);
}




