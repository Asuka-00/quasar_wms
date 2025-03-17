package com.wms.wmsend.controller.baseInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.SysDept;
import com.wms.wmsend.model.entity.SysUser;
import com.wms.wmsend.model.vo.DeptResultVo;
import com.wms.wmsend.service.SysDeptService;
import com.wms.wmsend.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/dept")
@Api(tags = "部门管理")
public class DeptController {


    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    @ApiOperation(value = "部门列表")
    private Result<IPage<DeptResultVo>> list(
            @ApiParam(value = "当前页", required = true) @RequestParam(value = "current") Long current,
            @ApiParam(value = "每页数量", required = true) @RequestParam(value = "size") Long size,
            @ApiParam(value = "部门名称") String deptName, @ApiParam(value = "单位ID") String unitId) {
        Page<SysDept> page = new Page<>(current, size);
        IPage<DeptResultVo> list = sysDeptService.listByPage(page, deptName, unitId);
        return Result.ok(list);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增/修改部门")
    private Result<String> saveOrUpdate(@ApiParam(value = "部门信息", required = true) @RequestBody SysDept dept) {
        //新增部门时查看部门是否已存在
        if (dept.getId() == null) {
            LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysDept::getDeptName, dept.getDeptName());
            SysDept one = sysDeptService.getOne(queryWrapper);
            if (one != null) throw new WmsException(ResultCodeEnum.DEPT_EXIST);
        }
        sysDeptService.saveOrUpdate(dept);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除部门")
    private Result<String> delete(@ApiParam(value = "部门ID", required = true) @RequestParam("id") Long id) {
        //region查询部门是否被引用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeptId, id);
        long count = sysUserService.count(wrapper);
        if (count > 0) throw new WmsException(ResultCodeEnum.DELETE_ERROR);
        //endregion
        sysDeptService.removeById(id);
        return Result.ok();
    }
}
