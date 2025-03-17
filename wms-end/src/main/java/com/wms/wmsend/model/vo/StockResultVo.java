package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "StockResultVo", description = "库存结果")
@Data
public class StockResultVo extends BaseResultVo {

    @Schema(description = "库存单号")
    private String documentNo;

    @Schema(description = "库存类型")
    private String documentType;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "状态")
    private Long status;

    @Schema(description = "审批人ID")
    private Long operatorUser;

    @Schema(description = "审批人名称")
    private String operatorUserName;

    @Schema(description = "备注")
    private String remark;
}
