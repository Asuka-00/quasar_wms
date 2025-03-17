package com.wms.wmsend.service;

import com.wms.wmsend.model.entity.InventoryDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.InventoryDetailResultVo;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【inventory_detail(盘点明细表)】的数据库操作Service
* @createDate 2025-03-05 16:50:12
*/
public interface InventoryDetailService extends IService<InventoryDetail> {

    List<InventoryDetailResultVo> listById(Long id);
}
