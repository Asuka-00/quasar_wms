package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.Inventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.InventoryPrintVo;
import com.wms.wmsend.model.vo.InventoryResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【inventory(盘点任务表)】的数据库操作Mapper
* @createDate 2025-03-04 13:03:29
* @Entity com.wms.wmsend.model.entity.Inventory
*/
public interface InventoryMapper extends BaseMapper<Inventory> {

    IPage<InventoryResultVo> listByPage(@Param("page") Page<Inventory> page,@Param("warehouseId") Long warehouseId,@Param("inventoryNo") String inventoryNo);

    InventoryPrintVo selectPrintData(@Param("id") Long id);
}




