package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class RealStockResultVo extends BaseResultVo{

    private String productName;

    private Long productId;

    private String warehouseName;

    private Long warehouseId;

    private Long quantity;

    private Long maxStock;

    private Long minStock;
}
