package com.wms.wmsend.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "ProductCategoryResultVo",description = "商品分类返回结果")
@Data
public class ProductCategoryResultVo extends BaseResultVo{

    @Schema(description = "商品分类名称")
    private String categoryName;
}
