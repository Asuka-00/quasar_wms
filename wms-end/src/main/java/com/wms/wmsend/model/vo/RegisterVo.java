package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class RegisterVo {
    private String nickname;
    private String username;
    private String password;
    private String captchaKey;
    private String captchaCode;
}
