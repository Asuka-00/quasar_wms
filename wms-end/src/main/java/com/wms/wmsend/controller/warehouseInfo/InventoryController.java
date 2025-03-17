package com.wms.wmsend.controller.warehouseInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.Inventory;
import com.wms.wmsend.model.entity.InventoryDetail;
import com.wms.wmsend.model.enums.DocNoEnum;
import com.wms.wmsend.model.enums.InventoryStatusEnum;
import com.wms.wmsend.model.vo.InventoryDetailResultVo;
import com.wms.wmsend.model.vo.InventoryRequestVo;
import com.wms.wmsend.model.vo.InventoryResultVo;
import com.wms.wmsend.model.vo.InventoryUpdateRequestVo;
import com.wms.wmsend.service.InventoryDetailService;
import com.wms.wmsend.service.InventoryService;
import freemarker.template.TemplateException;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/web/inventory")
@Api(tags = "库存盘点管理")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryDetailService inventoryDetailService;

    @GetMapping("/list")
    @ApiOperation(value = "库存盘点列表")
    public Result<IPage<InventoryResultVo>> list(
            @ApiParam(value = "页码", required = true) Long current,
            @ApiParam(value = "每页显示数量", required = true) Long size,
            @ApiParam(value = "仓库ID", required = false) Long warehouseId,
            @ApiParam(value = "盘点单号", required = false) String inventoryNo
    ){
        Page<Inventory> page = new Page<>(current, size);
        IPage<InventoryResultVo> list = inventoryService.listByPage(page, warehouseId, inventoryNo);
        return Result.ok(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "获取盘点单详情")
    public Result<List<InventoryDetailResultVo>> detail(@ApiParam(value = "盘点单ID", required = true) Long id){
        List<InventoryDetailResultVo> details = inventoryDetailService.listById(id);
        return Result.ok(details);
    }

    @PostMapping("save")
    @Transactional
    @ApiOperation(value = "新增库存盘点")
    public Result<String> save(@ApiParam(value = "盘点单实体", required = true) @RequestBody InventoryRequestVo inventoryRequestVo){
        inventoryRequestVo.setInventoryNo(DocNoEnum.INV.getPrefix()+System.currentTimeMillis());
        inventoryService.saveInventory(inventoryRequestVo);
        return Result.ok();
    }

    @PostMapping("update")
    @Transactional
    @ApiOperation(value = "更新库存盘点")
    public Result<String> update(@ApiParam(value = "盘点单实体", required = true)@RequestBody InventoryUpdateRequestVo inventoryRequestVo){
        inventoryService.updateInventory(inventoryRequestVo);
        return Result.ok();
    }

    @PostMapping("/inventory")
    @Transactional
    @ApiOperation(value = "盘点")
    public Result<String> inventory(@ApiParam(value = "盘点单实体", required = true)@RequestBody InventoryUpdateRequestVo inventoryRequestVo){
        inventoryService.inventory(inventoryRequestVo);
        return Result.ok();
    }

    @PostMapping("/complete")
    @Transactional
    @ApiOperation(value = "完成盘点")
    public Result<String> complete(@ApiParam(value = "盘点单ID", required = true) Long id){
        inventoryService.complete(id);
        return Result.ok();
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "取消盘点")
    public Result<String> cancel(@ApiParam(value = "盘点单ID", required = true) Long id){
        //当盘点单状态为进行中时，才能取消
        Inventory inventory = inventoryService.getById(id);
        if(inventory.getStatus().equals(InventoryStatusEnum.RUNNING.getCode())){
            LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Inventory::getId, id);
            updateWrapper.set(Inventory::getStatus, InventoryStatusEnum.CANCEL.getCode());
            inventoryService.update(updateWrapper);
        }else throw new WmsException(ResultCodeEnum.INVENTORY_STATUS_ERROR);
        return Result.ok();
    }

    @PostMapping("/print")
    @ApiOperation(value = "打印盘点单")
    public Result<String> print(@ApiParam(value = "盘点单ID", required = true) Long id) throws ServerException, TemplateException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String path = inventoryService.print(id);
        return Result.ok(path);
    }
}
