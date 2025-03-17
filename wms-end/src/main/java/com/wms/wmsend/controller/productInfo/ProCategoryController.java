package com.wms.wmsend.controller.productInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.Result;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.Product;
import com.wms.wmsend.model.entity.ProductCategory;
import com.wms.wmsend.model.vo.ProductCategoryResultVo;
import com.wms.wmsend.service.ProductCategoryService;
import com.wms.wmsend.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/product_category")
@Api(tags = "商品分类管理")
public class ProCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    @ApiOperation("分页查询商品分类")
    public Result<IPage<ProductCategoryResultVo>> list(
            @ApiParam(value = "当前页", required = true) Long current,
            @ApiParam(value = "每页显示条数", required = true) Long size,
            @ApiParam(value = "分类名称") String categoryName
    ){
        Page<ProductCategory> page = new Page<>(current, size);
        IPage<ProductCategoryResultVo> list = productCategoryService.listByPage(page, categoryName);
        return Result.ok(list);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation("新增或修改商品分类")
    public Result<String> saveOrUpdate(@ApiParam(value = "商品分类信息", required = true) @RequestBody ProductCategory productCategory){
        //新增商品分类不能重复
        if (productCategory.getId() == null){
            LambdaQueryWrapper<ProductCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProductCategory::getCategoryName, productCategory.getCategoryName());
            ProductCategory productCategory1 = productCategoryService.getOne(queryWrapper);
            if (productCategory1 != null) throw new WmsException(ResultCodeEnum.CATEGORY_NAME_EXIST);
        }else{
            //修改商品分类名称不能重复(不包含自己)
            LambdaQueryWrapper<ProductCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProductCategory::getCategoryName, productCategory.getCategoryName());
            queryWrapper.ne(ProductCategory::getId, productCategory.getId());
            ProductCategory productCategory1 = productCategoryService.getOne(queryWrapper);
            if (productCategory1 != null) throw new WmsException(ResultCodeEnum.CATEGORY_NAME_EXIST);
        }
        productCategoryService.saveOrUpdate(productCategory);
        return Result.ok();
    }

    @GetMapping("/delete")
    @ApiOperation("删除商品分类")
    public Result<String> delete(@ApiParam(value = "商品分类ID", required = true) Long id){
        //region 查询商品分类是否被商品引用
        if (productService.count(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id)) > 0)
            throw new WmsException(ResultCodeEnum.PRODUCT_TYPE_ERROR);
        //endregion

        productCategoryService.removeById(id);
        return Result.ok();
    }

}
