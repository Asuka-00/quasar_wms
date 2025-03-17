package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "用户查询返回实体")
public class UserResultVo extends BaseResultVo{
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "用户部门id")
    private Long deptId;
    @Schema(description = "用户部门")
    private String deptName;
    @Schema(description = "用户单位")
    private String unitName;
    @Schema(description = "用户角色")
    private String role;
    @Schema(description = "用户手机号")
    private String phone;
    @Schema(description = "用户邮箱")
    private String email;
}
