package com.wms.wmsend.model.vo;

import lombok.Data;

@Data
public class LoginResultVo {
    private String token;
    private String refreshToken;
    private String role;
    private String nickname;
    private Long id;
    private String username;
}
