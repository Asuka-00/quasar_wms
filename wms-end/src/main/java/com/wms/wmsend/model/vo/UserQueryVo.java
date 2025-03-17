package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询实体")
public class UserQueryVo {
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "部门id")
    private String deptId;
    @Schema(description = "单位id")
    private String unitId;
}
