package com.wms.wmsend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class StockHistoryResultVo {

    private String warehouseName;

    private String productName;

    private Long diff;

    private String remark;

    private String actionUsername;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actionTime;
}
