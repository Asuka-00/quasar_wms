package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.StockAlert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.StockAlertResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【stock_alert(库存预警表)】的数据库操作Mapper
* @createDate 2025-03-04 12:48:46
* @Entity com.wms.wmsend.model.entity.StockAlert
*/
public interface StockAlertMapper extends BaseMapper<StockAlert> {

    IPage<StockAlertResultVo> listByPage(@Param("page") Page<StockAlert> page,@Param("productId") String productId,@Param("warehouseId") String warehouseId);
}




