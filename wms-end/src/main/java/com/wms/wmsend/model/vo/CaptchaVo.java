package com.wms.wmsend.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel(description = "图像验证码")
@AllArgsConstructor
public class CaptchaVo {

    @ApiModelProperty(value="验证码图片信息")
    private String image;

    @ApiModelProperty(value="验证码key")
    private String key;
}
