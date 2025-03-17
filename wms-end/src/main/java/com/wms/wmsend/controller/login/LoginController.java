package com.wms.wmsend.controller.login;

import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.util.JwtUtil;
import com.wms.wmsend.model.vo.CaptchaVo;
import com.wms.wmsend.model.vo.LoginResultVo;
import com.wms.wmsend.model.vo.LoginVo;
import com.wms.wmsend.model.vo.RegisterVo;
import com.wms.wmsend.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web")
@Api(tags = "登录管理")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<LoginResultVo> login(@ApiParam(value = "登录实体", required = true)@RequestBody LoginVo loginVo) {
        return Result.ok(sysUserService.login(loginVo));
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    public Result<String> register(@ApiParam(value = "注册实体", required = true)@RequestBody RegisterVo registerVo) {
        sysUserService.register(registerVo);
        return Result.ok();
    }

    @ApiOperation(value = "获取验证码")
    @GetMapping("/login/captcha")
    public Result<CaptchaVo> getCaptcha(){
        CaptchaVo captchaVo = sysUserService.getCaptcha();
        return Result.ok(captchaVo);
    }

    @GetMapping("/login/refreshToken")
    @ApiOperation("刷新token")
    public Result<String> refreshToken(@RequestParam String refreshToken) {
        Claims claims = JwtUtil.parseToken(refreshToken);
        Long userId = claims.get("userId",Long.class);
        String username = claims.get("username",String.class);
        return Result.ok(JwtUtil.createToken(userId, username));
    }

}
