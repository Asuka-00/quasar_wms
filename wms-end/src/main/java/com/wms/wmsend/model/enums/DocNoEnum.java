package com.wms.wmsend.model.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum DocNoEnum {

    INV("INV","盘点单前缀"),
    STOCK_IN("STI","入库单前缀"),
    STOCK_OUT("STO","出库单前缀");

    private String prefix;

    private String remark;

    DocNoEnum(String prefix, String remark) {
        this.prefix = prefix;
        this.remark = remark;
    }
}
