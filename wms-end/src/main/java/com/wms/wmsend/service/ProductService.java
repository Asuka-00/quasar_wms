package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.ProductResultVo;

/**
* @author Ayanami
* @description 针对表【product(商品信息表)】的数据库操作Service
* @createDate 2025-03-03 16:43:37
*/
public interface ProductService extends IService<Product> {

    IPage<ProductResultVo> listByPage(Page<Product> page, String productName, String categoryId,String warehouseId);
}
