package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "DeptResultVo",title = "部门返回结果")
@Data
public class DeptResultVo extends BaseResultVo {

    @Schema(name = "deptName",title = "部门名称")
    private String deptName;

    @Schema(name = "deptCode",title = "单位名称")
    private String unitName;

    @Schema(name = "unitId",title = "单位ID")
    private Long unitId;
}
