package com.wms.wmsend.controller.productInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.Product;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.entity.StockDocItem;
import com.wms.wmsend.model.vo.ProductResultVo;
import com.wms.wmsend.service.ProductService;
import com.wms.wmsend.service.RealtimeStockService;
import com.wms.wmsend.service.StockDocItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/product")
@Api(tags = "商品管理")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RealtimeStockService realtimeStockService;

    @Autowired
    private StockDocItemService stockDocItemService;

    @GetMapping("/list")
    @ApiOperation(value = "商品列表")
    public Result<IPage<ProductResultVo>> list(
            @ApiParam(value = "当前页", required = true) Long current,
            @ApiParam(value = "每页显示条数", required = true) Long size,
            @ApiParam(value = "商品名称") String productName,
            @ApiParam(value = "分类ID") String categoryId,
            @ApiParam(value = "归属仓库") String warehouseId
    ){
        Page<Product> page = new Page<>(current,size);
        IPage<ProductResultVo> list = productService.listByPage(page, productName, categoryId,warehouseId);
        return Result.ok(list);
    }

    @PostMapping("saveOrUpdate")
    @ApiOperation(value = "新增或修改商品")
    public Result<String> saveOrUpdate(@ApiParam(value = "商品信息", required = true) @RequestBody Product product){
        //新增商品时，判断商品是否已存在
        if(product.getId() == null){
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getProductName, product.getProductName());
            Product one = productService.getOne(queryWrapper);
            if(one != null) throw new WmsException(ResultCodeEnum.PRODUCT_EXIST);
        }else{
            //修改商品时，判断商品是否已存在
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getProductName, product.getProductName()).ne(Product::getId, product.getId());
            Product one = productService.getOne(queryWrapper);
            if(one != null) throw new WmsException(ResultCodeEnum.PRODUCT_EXIST);
        }
        productService.saveOrUpdate(product);
        return Result.ok();
    }

    @GetMapping("delete")
    @ApiOperation(value = "删除商品")
    public Result<String> delete(@ApiParam(value = "商品ID", required = true) Long id){
        //region 查询仓库是否有商品
        if(realtimeStockService.count(new LambdaQueryWrapper<RealtimeStock>().eq(RealtimeStock::getProductId, id)) > 0)
            throw new WmsException(ResultCodeEnum.PRODUCT_USED_WAREHOUSE);
        //endregion

        //商品是否被入库单据引用
        if (stockDocItemService.count(new LambdaQueryWrapper<StockDocItem>().eq(StockDocItem::getProductId, id)) > 0)
            throw new WmsException(ResultCodeEnum.PRODUCT_USED_STOCK_DOC);

        productService.removeById(id);
        return Result.ok();
    }
}
