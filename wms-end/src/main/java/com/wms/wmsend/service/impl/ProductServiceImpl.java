package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.Product;
import com.wms.wmsend.model.vo.ProductResultVo;
import com.wms.wmsend.service.ProductService;
import com.wms.wmsend.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【product(商品信息表)】的数据库操作Service实现
* @createDate 2025-03-03 16:43:37
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

    @Autowired
    private ProductMapper productMapper;

    @Override
    public IPage<ProductResultVo> listByPage(Page<Product> page, String productName, String categoryId,String warehouseId) {
        return productMapper.listByPage(page, productName, categoryId,warehouseId);
    }
}




