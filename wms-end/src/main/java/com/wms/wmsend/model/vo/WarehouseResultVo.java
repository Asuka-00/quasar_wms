package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "WarehouseResultVo",description = "仓库返回结果")
@Data
public class WarehouseResultVo extends BaseResultVo{

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "所属单位名称")
    private String unitName;

    @Schema(description = "所属单位ID")
    private String unitId;

    @Schema(description = "仓库地址")
    private String address;
}
