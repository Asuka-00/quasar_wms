package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wms.wmsend.common.constant.RedisConstant;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.common.util.JwtUtil;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.SysUser;
import com.wms.wmsend.model.vo.*;
import com.wms.wmsend.service.SysUserService;
import com.wms.wmsend.mapper.SysUserMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author Ayanami
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2025-03-03 14:28:19
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public IPage<UserResultVo> listByPage(Page<SysUser> page, UserQueryVo queryVo) {
        return sysUserMapper.listByPage(page, queryVo);
    }

    @Override
    public CaptchaVo getCaptcha() {
        //生成验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        //存入Redis
        String code = specCaptcha.text().toLowerCase();
        String key = RedisConstant.LOGIN_PREFIX + UUID.randomUUID();
        String image = specCaptcha.toBase64();
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);

        return new CaptchaVo(image, key);
    }

    @Override
    public LoginResultVo login(LoginVo loginVo) {
        //验证码是否正确
        String code = stringRedisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if(code==null){
            throw new WmsException(ResultCodeEnum.CODE_EXPIRED);
        } else if (!code.equals(loginVo.getCaptchaCode().toLowerCase())) {
            throw new WmsException(ResultCodeEnum.CODE_ERROR);
        }
        //用户与密码是否正确
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getUsername,loginVo.getUsername());
        SysUser sysUser = sysUserMapper.selectOne(sysUserLambdaQueryWrapper);

        if(sysUser==null) throw new WmsException(ResultCodeEnum.USERNAME_NULL);
        if(!sysUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword())))throw new WmsException(ResultCodeEnum.PASSWORD_ERROR);

        LoginResultVo loginResultVo = new LoginResultVo();
        loginResultVo.setToken(JwtUtil.createToken(sysUser.getId(), sysUser.getUsername()));
        loginResultVo.setRole(sysUser.getRole());
        loginResultVo.setNickname(sysUser.getNickname());
        loginResultVo.setId(sysUser.getId());
        loginResultVo.setUsername(sysUser.getUsername());
        loginResultVo.setRefreshToken(JwtUtil.createRefreshToken(sysUser.getId(), sysUser.getUsername()));

        return loginResultVo;
    }

    @Override
    public void register(RegisterVo registerVo) {
        //验证码是否正确
        String code = stringRedisTemplate.opsForValue().get(registerVo.getCaptchaKey());
        if(code==null){
            throw new WmsException(ResultCodeEnum.CODE_EXPIRED);
        } else if (!code.equals(registerVo.getCaptchaCode().toLowerCase())) {
            throw new WmsException(ResultCodeEnum.CODE_ERROR);
        }

        //用户名是否已存在
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(SysUser::getUsername,registerVo.getUsername());
        SysUser sysUser = sysUserMapper.selectOne(sysUserLambdaQueryWrapper);
        if(sysUser!=null) throw new WmsException(ResultCodeEnum.USERNAME_EXIST);

        SysUser newUser = new SysUser();
        newUser.setUsername(registerVo.getUsername());
        newUser.setPassword(DigestUtils.md5Hex(registerVo.getPassword()));
        newUser.setNickname(registerVo.getNickname());
        newUser.setRole("user");
        sysUserMapper.insert(newUser);
    }
}




