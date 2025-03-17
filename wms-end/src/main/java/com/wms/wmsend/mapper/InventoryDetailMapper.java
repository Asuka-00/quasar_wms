package com.wms.wmsend.mapper;

import com.wms.wmsend.model.entity.InventoryDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.InventoryDetailResultVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【inventory_detail(盘点明细表)】的数据库操作Mapper
* @createDate 2025-03-05 16:50:12
* @Entity com.wms.wmsend.model.entity.InventoryDetail
*/
public interface InventoryDetailMapper extends BaseMapper<InventoryDetail> {

    List<InventoryDetailResultVo> selectPrintList(@Param("id") Long id);
}




