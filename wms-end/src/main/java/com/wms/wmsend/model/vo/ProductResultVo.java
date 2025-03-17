package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class ProductResultVo extends BaseResultVo{

    private String productName;

    private String categoryName;

    private Long categoryId;

    private String warehouseName;

    private Long warehouseId;
}
