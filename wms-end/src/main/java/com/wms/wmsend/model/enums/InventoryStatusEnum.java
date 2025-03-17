package com.wms.wmsend.model.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum InventoryStatusEnum {
    RUNNING("进行中", 0L),
    APPROVED("已盘点",1L),
    COMPLETED("已完成",2L),
    CANCEL("已取消",3L);

    private String desc;
    private Long code;

    InventoryStatusEnum(String desc, Long code) {
        this.desc = desc;
        this.code = code;
    }

    //根据code获取desc
    public static String getDescByCode(Long code) {
        for (InventoryStatusEnum inventoryStatusEnum : InventoryStatusEnum.values()) {
            if (inventoryStatusEnum.getCode().equals(code)) {
                return inventoryStatusEnum.getDesc();
            }
        }
        return null;
    }
}
