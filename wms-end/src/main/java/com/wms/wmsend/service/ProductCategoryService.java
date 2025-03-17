package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.ProductCategoryResultVo;

/**
* @author Ayanami
* @description 针对表【product_category(商品分类表)】的数据库操作Service
* @createDate 2025-03-03 16:28:31
*/
public interface ProductCategoryService extends IService<ProductCategory> {

    IPage<ProductCategoryResultVo> listByPage(Page<ProductCategory> page, String categoryName);
}
