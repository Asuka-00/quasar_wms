package com.wms.wmsend.controller.baseInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.SysDept;
import com.wms.wmsend.model.entity.SysUnit;
import com.wms.wmsend.model.vo.UnitResultVo;
import com.wms.wmsend.service.SysDeptService;
import com.wms.wmsend.service.SysUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/unit")
@Api(tags = "单位管理")
public class UnitController {

    @Autowired
    private SysUnitService sysUnitService;

    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("/list")
    @ApiOperation(value = "单位列表")
    public Result<IPage<UnitResultVo>> list(
            @ApiParam(value = "当前页码", required = true) @RequestParam(value = "current") Long current,
            @ApiParam(value = "每页数量", required = true) @RequestParam(value = "size") Long size,
            @ApiParam(value = "单位名称") String unitName ) {
        Page<SysUnit> page = new Page<>(current, size);
        IPage<UnitResultVo> list = sysUnitService.listByPage(page, unitName);
        return Result.ok(list);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增/修改单位")
    public Result<String> saveOrUpdate(@ApiParam(value = "单位信息", required = true) @RequestBody SysUnit unit) {
        //新增单位不能重复
        if (unit.getId() == null) {
            LambdaQueryWrapper<SysUnit> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUnit::getUnitName, unit.getUnitName());
            SysUnit unit1 = sysUnitService.getOne(queryWrapper);
            if (unit1 != null) throw new WmsException(ResultCodeEnum.UNIT_NAME_EXIST);
        }else{
            //修改单位名称不能重复(不包含自己)
            LambdaQueryWrapper<SysUnit> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUnit::getUnitName, unit.getUnitName());
            queryWrapper.ne(SysUnit::getId, unit.getId());
            SysUnit unit1 = sysUnitService.getOne(queryWrapper);
            if (unit1 != null) throw new WmsException(ResultCodeEnum.UNIT_NAME_EXIST);
        }
        sysUnitService.saveOrUpdate(unit);
        return Result.ok();
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除单位")
    public Result<String> delete(@ApiParam(value = "单位ID", required = true) @RequestParam("id") Long id) {
        //region查询单位是否被引用
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getUnitId, id);
        long count = sysDeptService.count(wrapper);
        if (count > 0) throw new WmsException(ResultCodeEnum.DELETE_ERROR);
        //endregion
        sysUnitService.removeById(id);
        return Result.ok();
    }

}
