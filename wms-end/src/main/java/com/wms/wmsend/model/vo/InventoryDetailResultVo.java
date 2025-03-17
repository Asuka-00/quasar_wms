package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class InventoryDetailResultVo {
    public Long id;

    public String productName;

    public Integer productId;

    public Integer actualQuantity;

    public Integer systemQuantity;

    public Integer diff;

    public String status;
}
