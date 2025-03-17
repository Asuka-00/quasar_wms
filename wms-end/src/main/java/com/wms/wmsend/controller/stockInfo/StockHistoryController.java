package com.wms.wmsend.controller.stockInfo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.model.entity.StockHistory;
import com.wms.wmsend.model.vo.StockHistoryResultVo;
import com.wms.wmsend.service.StockHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/stockHistory")
@Api(tags = "库存历史")
public class StockHistoryController {

    @Autowired
    private StockHistoryService stockHistoryService;

    @GetMapping("/list")
    public Result<IPage<StockHistoryResultVo>> list (
            @ApiParam(value = "当前页", required = true) Long current,
            @ApiParam(value = "每页显示条数", required = true) Long size,
            @ApiParam(value = "仓库ID") String warehouseId,
            @ApiParam(value = "产品ID") String productId,
            @ApiParam(value = "操作类型") String actionName,
            @ApiParam(value = "单据号") String docNo
    ) {
        Page<StockHistory> page = new Page<>(current, size);
        IPage<StockHistoryResultVo> stockHistoryResultVoIPage = stockHistoryService.listByPage(page, warehouseId, productId,actionName,docNo);
        return Result.ok(stockHistoryResultVoIPage);
    }

}
