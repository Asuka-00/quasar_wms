package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class StockDocDetailResultVo extends BaseResultVo{

    private String documentNo;

    private Long documentId;

    private Long productId;

    private String productName;

    private Long quantity;
}
