package com.wms.wmsend.controller.warehouseInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.Product;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.entity.Warehouse;
import com.wms.wmsend.model.vo.WarehouseResultVo;
import com.wms.wmsend.service.ProductService;
import com.wms.wmsend.service.RealtimeStockService;
import com.wms.wmsend.service.WarehouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/warehouse")
@Api(tags = "仓库管理")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private RealtimeStockService realtimeStockService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    @ApiOperation(value = "仓库列表")
    public Result<IPage<WarehouseResultVo>> list(
            @ApiParam(value = "当前页", required = true) Integer current,
            @ApiParam(value = "每页显示条数", required = true) Integer size,
            @ApiParam(value = "仓库名称", required = false) String warehouseName,
            @ApiParam(value = "所属单位ID", required = false) String unitId
    ){
        Page<Warehouse> page = new Page<>(current,size);
        IPage<WarehouseResultVo> list = warehouseService.listByPage(page,warehouseName,unitId);
        return Result.ok(list);
    }

    @PostMapping("saveOrUpdate")
    @ApiOperation(value = "新增或修改仓库")
    public Result<String> saveOrUpdate(@ApiParam(value = "仓库信息") @RequestBody Warehouse warehouse){
        //新增仓库不能重复
        if (warehouse.getId() == null){
            LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Warehouse::getWarehouseName,warehouse.getWarehouseName());
            Warehouse warehouse1 = warehouseService.getOne(queryWrapper);
            if (warehouse1 != null)throw new WmsException(ResultCodeEnum.WAREHOUSE_NAME_EXIST);
        }else{
            //修改仓库不能重复(不包含自己)
            LambdaQueryWrapper<Warehouse> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Warehouse::getWarehouseName,warehouse.getWarehouseName());
            queryWrapper.ne(Warehouse::getId,warehouse.getId());
            Warehouse warehouse1 = warehouseService.getOne(queryWrapper);
            if (warehouse1 != null)throw new WmsException(ResultCodeEnum.WAREHOUSE_NAME_EXIST);
        }
        warehouseService.saveOrUpdate(warehouse);
        return Result.ok();
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除仓库")
    public Result<String> delete(@ApiParam(value = "仓库ID", required = true) @RequestParam("id") Long id){
        //region 查询仓库是否被引用
        if (realtimeStockService.count(new LambdaQueryWrapper<RealtimeStock>().eq(RealtimeStock::getWarehouseId,id)) > 0)
            throw new WmsException(ResultCodeEnum.WAREHOUSE_USED_STOCK);
        //endregion

        //region 查询仓库是否被商品引用
        if (productService.count(new LambdaQueryWrapper<Product>().eq(Product::getWarehouseId,id)) > 0)
            throw new WmsException(ResultCodeEnum.WAREHOUSE_USED_PRODUCT);
        //endregion
        warehouseService.removeById(id);
        return Result.ok();
    }
}
