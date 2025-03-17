package com.wms.wmsend.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("登录入参")
public class LoginVo {
    private String username;
    private String password;
    private String captchaKey;
    private String captchaCode;
}
