package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class StockAlertResultVo extends BaseResultVo{

    private Long categoryId;

    private String categoryName;

    private String productName;

    private Long productId;

    private String warehouseName;

    private Long warehouseId;

    private Integer minStock;

    private Integer maxStock;

    private String sendType;

    private Integer sendTo;

    private String sendToName;

}
