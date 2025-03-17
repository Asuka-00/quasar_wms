package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class StockDocQueryVo {
    private String documentNo;
    private String documentType;
    private String status;
    private Long warehouseId;
}
