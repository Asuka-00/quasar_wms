package com.wms.wmsend.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseEntity implements Serializable {

    @TableId(value = "id", type= IdType.AUTO)
    private Long id;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @TableField(value = "created_by",fill = FieldFill.INSERT)
    private String createdBy;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @TableField(value = "updated_by",fill = FieldFill.UPDATE)
    private String updatedBy;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @TableField(value = "created_time",fill = FieldFill.INSERT)
    private Date createdTime;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @TableField(value = "updated_time",fill = FieldFill.UPDATE)
    private Date updatedTime;
}
