package com.wms.wmsend.controller.warehouseInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.model.entity.StockAlert;
import com.wms.wmsend.model.entity.StockAlertDetail;
import com.wms.wmsend.model.vo.StockAlertResultVo;
import com.wms.wmsend.service.StockAlertDetailService;
import com.wms.wmsend.service.StockAlertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/whAlert")
@Api(tags = "库存预警")
public class WhAlertController {


    @Autowired
    private StockAlertService stockAlertService;

    @Autowired
    private StockAlertDetailService stockAlertDetailService;


    @GetMapping("/list")
    @ApiOperation(value = "库存预警列表")
    private Result<IPage<StockAlertResultVo>> list(
            @ApiParam(value = "当前页码", required = true) Long current,
            @ApiParam(value = "每页显示数量", required = true) Long size,
            @ApiParam(value = "商品ID") String productId,
            @ApiParam(value = "仓库ID") String warehouseId
    ){
        Page<StockAlert> page = new Page<>(current, size);
        IPage<StockAlertResultVo> list = stockAlertService.listByPage(page, productId, warehouseId);
        return Result.ok(list);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增或修改库存预警")
    private Result<String> saveOrUpdate(@ApiParam(value = "库存预警对象", required = true) @RequestBody StockAlert stockAlert){
        stockAlertService.saveOrUpdate(stockAlert);
        return Result.ok();
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除库存预警")
    private Result<String> delete(@ApiParam(value = "库存预警ID", required = true) String id){
        stockAlertService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/getAlert")
    @ApiOperation(value = "获取库存预警通知")
    private Result<List<StockAlertDetail>> getAlert(@ApiParam(value = "用户ID", required = true) Long id){
        LambdaQueryWrapper<StockAlertDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockAlertDetail::getSendTo, id);
        queryWrapper.eq(StockAlertDetail::getStatus, 0);
        List<StockAlertDetail> stockAlertDetailList = stockAlertDetailService.list(queryWrapper);
        return Result.ok(stockAlertDetailList);
    }

    @GetMapping("/readAlert")
    @ApiOperation(value = "标记库存预警通知为已读")
    private Result<String> readAlert(@ApiParam(value = "库存预警通知ID", required = true) Long id){
        StockAlertDetail stockAlertDetail = stockAlertDetailService.getById(id);
        stockAlertDetail.setStatus(1L);
        stockAlertDetailService.updateById(stockAlertDetail);
        return Result.ok();
    }

}
