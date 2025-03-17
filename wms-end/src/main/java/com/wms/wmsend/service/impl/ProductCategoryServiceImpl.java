package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.ProductCategory;
import com.wms.wmsend.model.vo.ProductCategoryResultVo;
import com.wms.wmsend.service.ProductCategoryService;
import com.wms.wmsend.mapper.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【product_category(商品分类表)】的数据库操作Service实现
* @createDate 2025-03-03 16:28:31
*/
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory>
    implements ProductCategoryService{

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Override
    public IPage<ProductCategoryResultVo> listByPage(Page<ProductCategory> page, String categoryName) {
        return productCategoryMapper.listByPage(page, categoryName);
    }
}




