package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.ProductCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.ProductCategoryResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【product_category(商品分类表)】的数据库操作Mapper
* @createDate 2025-03-03 16:28:31
* @Entity com.wms.wmsend.model.entity.ProductCategory
*/
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    IPage<ProductCategoryResultVo> listByPage(@Param("page") Page<ProductCategory> page,@Param("categoryName") String categoryName);
}




