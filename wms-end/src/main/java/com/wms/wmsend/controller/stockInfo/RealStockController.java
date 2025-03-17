package com.wms.wmsend.controller.stockInfo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.vo.RealStockQueryVo;
import com.wms.wmsend.model.vo.RealStockResultVo;
import com.wms.wmsend.service.RealtimeStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/realStock")
@Api(tags = "实时库存")
public class RealStockController {

    @Autowired
    private RealtimeStockService realtimeStockService;


    @PostMapping("/list")
    @ApiOperation(value = "实时库存列表")
    private Result<IPage<RealStockResultVo>> list(
            @ApiParam(value = "当前页", required = true) Long current,
            @ApiParam(value = "每页显示条数", required = true) Long size,
            @ApiParam(value = "查询条件") @RequestBody RealStockQueryVo realStockQueryVo
    ){
        Page<RealtimeStock> page = new Page<>(current, size);
        IPage<RealStockResultVo> list = realtimeStockService.listByPage(page, realStockQueryVo);
        return Result.ok(list);
    }

}
