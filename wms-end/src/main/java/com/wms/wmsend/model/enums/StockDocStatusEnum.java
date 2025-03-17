package com.wms.wmsend.model.enums;

import lombok.Getter;

@Getter
public enum StockDocStatusEnum {
    ONGOING("进行中", 0L),
    APPROVED("已审批",1L),
    COMPLETED("已完成",2L),
    CANCELLED("已取消",3L);

    private String desc;
    private Long code;

    StockDocStatusEnum(String desc, Long code) {
        this.desc = desc;
        this.code = code;
    }

}
