package com.wms.wmsend.controller.baseInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.SysUser;
import com.wms.wmsend.model.vo.UserQueryVo;
import com.wms.wmsend.model.vo.UserResultVo;
import com.wms.wmsend.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/user")
@Api(tags = "用户信息管理")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/list")
    @ApiOperation(value = "用户列表")
    public Result<IPage<UserResultVo>> list(
            @ApiParam(value = "当前页码", required = true) @RequestParam("current") Long current,
            @ApiParam(value = "每页数量", required = true) @RequestParam("size") Long size,
            @ApiParam(value = "查询条件") @RequestBody UserQueryVo queryVo) {
        Page<SysUser> page = new Page<>(current, size);
        IPage<UserResultVo> list = sysUserService.listByPage(page, queryVo);
        return Result.ok(list);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增/修改用户")
    public Result<String> saveOrUpdate(@ApiParam(value = "用户信息", required = true) @RequestBody SysUser user) {
        //用户不能重复
        if (user.getId() == null) {
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getUsername, user.getUsername());
            SysUser user1 = sysUserService.getOne(queryWrapper);
            if (user1 != null) throw new WmsException(ResultCodeEnum.USERNAME_EXIST);
        }else{
            //修改用户名不能重复(不包含自己)
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getUsername, user.getUsername());
            queryWrapper.ne(SysUser::getId, user.getId());
            SysUser user1 = sysUserService.getOne(queryWrapper);
            if (user1 != null) throw new WmsException(ResultCodeEnum.USERNAME_EXIST);
        }
        //加密密码
        user.setPassword("123456");//默认密码123456
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        sysUserService.saveOrUpdate(user);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    public Result<String> delete(@ApiParam(value = "用户ID", required = true) @RequestParam("id") long id) {
        sysUserService.removeById(id);
        return Result.ok();
    }
}
