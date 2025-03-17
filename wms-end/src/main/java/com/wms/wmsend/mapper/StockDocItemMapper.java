package com.wms.wmsend.mapper;

import com.wms.wmsend.model.entity.StockDocItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.StockDocDetailResultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【stock_doc_item(单据明细表)】的数据库操作Mapper
* @createDate 2025-03-03 17:02:07
* @Entity com.wms.wmsend.model.entity.StockDocItem
*/
public interface StockDocItemMapper extends BaseMapper<StockDocItem> {

    List<StockDocDetailResultVo> detail(@Param("id") Long id);
}




