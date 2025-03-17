package com.wms.wmsend.service;

import com.wms.wmsend.model.entity.StockDocItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.StockDocDetailResultVo;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【stock_doc_item(单据明细表)】的数据库操作Service
* @createDate 2025-03-03 17:02:07
*/
public interface StockDocItemService extends IService<StockDocItem> {

    List<StockDocDetailResultVo> detail(Long id);
}
