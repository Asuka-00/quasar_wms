package com.wms.wmsend.model.vo;

import com.wms.wmsend.model.entity.InventoryDetail;
import lombok.Data;

import java.util.List;

@Data
public class InventoryRequestVo{

    private String inventoryNo;

    private Long warehouseId;

    private Long status;

    private String remark;

    private List<InventoryDetail> items;
}
