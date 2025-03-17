package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.Warehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.WarehouseResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【warehouse(仓库信息表)】的数据库操作Mapper
* @createDate 2025-03-03 16:07:56
* @Entity com.wms.wmsend.model.entity.Warehouse
*/
public interface WarehouseMapper extends BaseMapper<Warehouse> {

    IPage<WarehouseResultVo> listByPage(@Param("page") Page<Warehouse> page, @Param("warehouseName") String warehouseName, @Param("unitId") String unitId);
}




