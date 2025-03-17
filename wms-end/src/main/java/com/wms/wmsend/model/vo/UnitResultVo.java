package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "单位返回结果")
public class UnitResultVo extends BaseResultVo {

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "统一社会信用代码/编码")
    private String unitCode;
}
