package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class InventoryResultVo extends BaseResultVo{


    public String inventoryNo;

    public String warehouseName;

    public Long warehouseId;

    public Long status;

    public String remark;

}
