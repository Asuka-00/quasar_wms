package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.Product;
import com.wms.wmsend.model.vo.ProductResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【product(商品信息表)】的数据库操作Mapper
* @createDate 2025-03-08 00:15:15
* @Entity com.wms.wmsend.model.entity.Product
*/
public interface ProductMapper extends BaseMapper<Product> {

    IPage<ProductResultVo> listByPage(@Param("page") Page<Product> page,
                                      @Param("productName") String productName,
                                      @Param("categoryId") String categoryId,
                                      @Param("warehouseId") String warehouseId);
}




