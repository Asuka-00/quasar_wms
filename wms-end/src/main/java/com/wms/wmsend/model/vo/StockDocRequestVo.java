package com.wms.wmsend.model.vo;

import com.wms.wmsend.model.entity.StockDocItem;
import lombok.Data;

import java.util.List;

@Data
public class StockDocRequestVo {
    private String documentNo;
    private String documentType;
    private Long warehouseId;
    private Long status;
    private Long operatorUser;
    private String remark;
    private List<StockDocItem> items;
}
