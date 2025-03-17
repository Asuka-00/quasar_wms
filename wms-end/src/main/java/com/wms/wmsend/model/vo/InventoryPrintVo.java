package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class InventoryPrintVo {

    public String inventoryNo;

    public String warehouseName;

    public Long warehouseId;

    public Long status;

    public String remark;

    public String statusDesc;

    public String createdName;

    public String createdTime;
}
