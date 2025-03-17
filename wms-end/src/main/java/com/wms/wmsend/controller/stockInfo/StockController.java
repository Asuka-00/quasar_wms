package com.wms.wmsend.controller.stockInfo;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.StockDoc;
import com.wms.wmsend.model.enums.DocNoEnum;
import com.wms.wmsend.model.enums.StockDocStatusEnum;
import com.wms.wmsend.model.vo.*;
import com.wms.wmsend.service.StockDocItemService;
import com.wms.wmsend.service.StockDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/web/stock")
@Api(tags = "库存管理")
public class StockController {

    @Autowired
    private StockDocService stockDocService;

    @Autowired
    private StockDocItemService stockDocItemService;

    @PostMapping("/list")
    @ApiOperation("库存单据列表")
    public Result<IPage<StockResultVo>> list(
            @ApiParam(value = "当前页", required = true) @RequestParam Long current,
            @ApiParam(value = "每页显示条数", required = true) @RequestParam Long size,
            @ApiParam(value = "单据信息") @RequestBody StockDocQueryVo stockDocQueryVo
    ){
        Page<StockDoc> page = new Page<>(current, size);
        IPage<StockResultVo> list = stockDocService.listByPage(page, stockDocQueryVo);
        return Result.ok(list);
    }

    @GetMapping("/detail")
    @ApiOperation("获取库存单据详情")
    public Result<List<StockDocDetailResultVo>> detail(@ApiParam(value = "单据ID", required = true) @RequestParam("id") Long id){
        List<StockDocDetailResultVo> detail = stockDocItemService.detail(id);
        return Result.ok(detail);
    }

    @PostMapping("/saveDoc")
    @Transactional
    @ApiOperation("保存库存单据")
    public Result<String> saveDoc(@RequestBody StockDocRequestVo stockDocRequestVo){
        stockDocRequestVo.setStatus(StockDocStatusEnum.ONGOING.getCode());
        //判断单据类型
        if(stockDocRequestVo.getDocumentType().equals("IN")) {
            stockDocRequestVo.setDocumentNo(DocNoEnum.STOCK_IN.getPrefix()+System.currentTimeMillis());//当前时间戳
        } else if(stockDocRequestVo.getDocumentType().equals("OUT")) {
            stockDocRequestVo.setDocumentNo(DocNoEnum.STOCK_OUT.getPrefix()+System.currentTimeMillis());
        } else {
            throw new WmsException(ResultCodeEnum.STOCK_DOC_TYPE_ERROR);
        }

        stockDocService.saveDoc(stockDocRequestVo);
        return Result.ok();
    }

    @PostMapping("/updateDoc")
    @Transactional
    @ApiOperation("更新库存单据")
    public Result<String> updateDoc(@RequestBody StockDocUpdateRequestVo stockDocRequestVo){
        stockDocService.updateDoc(stockDocRequestVo);
        return Result.ok();
    }

    @PostMapping("/audit")
    @ApiOperation("审核库存单据")
    public Result<String> audit(@ApiParam(value = "单据ID", required = true) @RequestParam("id") Long id){
        LambdaUpdateWrapper<StockDoc> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StockDoc::getId, id);
        updateWrapper.set(StockDoc::getStatus, StockDocStatusEnum.APPROVED.getCode());
        stockDocService.update(updateWrapper);
        return Result.ok();
    }

    @PostMapping("/cancel")
    @ApiOperation("取消库存单据")
    public Result<String> cancel(@ApiParam(value = "单据ID", required = true) @RequestParam("id") Long id){
        //单据已审核或已完成，不允许取消
        StockDoc stockDoc = stockDocService.getById(id);
        if(stockDoc.getStatus().equals(StockDocStatusEnum.APPROVED.getCode()) || stockDoc.getStatus().equals(StockDocStatusEnum.COMPLETED.getCode())){
            throw new WmsException(ResultCodeEnum.STOCK_DOC_CANNOT_CANCEL);
        }

        LambdaUpdateWrapper<StockDoc> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StockDoc::getId, id);
        updateWrapper.set(StockDoc::getStatus, StockDocStatusEnum.CANCELLED.getCode());
        stockDocService.update(updateWrapper);
        return Result.ok();
    }

    @PostMapping("/complete")
    @Transactional
    @ApiOperation("完成库存单据")
    public Result<String> complete(@ApiParam(value = "单据ID", required = true) @RequestParam("id") Long id){
        //更新单据状态
        LambdaUpdateWrapper<StockDoc> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StockDoc::getId, id);
        updateWrapper.set(StockDoc::getStatus, StockDocStatusEnum.COMPLETED.getCode());
        stockDocService.update(updateWrapper);
        //更新库存
        stockDocService.updateStock(id);
        return Result.ok();
    }
}
