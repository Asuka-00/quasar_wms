package com.wms.wmsend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "基础返回结果")
public class BaseResultVo {

    @Schema(description = "主键ID")
    public Long id;
    @Schema(description = "创建人")
    public String createdBy;
    @Schema(description = "创建时间")
    public Date createdTime;
    @Schema(description = "更新人")
    public String updatedBy;
    @Schema(description = "更新时间")
    public Date updatedTime;
}
