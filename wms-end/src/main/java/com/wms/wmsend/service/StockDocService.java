package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.StockDoc;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.*;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【stock_doc(库存单据表)】的数据库操作Service
* @createDate 2025-03-03 17:08:30
*/
public interface StockDocService extends IService<StockDoc> {

    IPage<StockResultVo> listByPage(Page<StockDoc> page, StockDocQueryVo stockDocQueryVo);

    void saveDoc(StockDocRequestVo stockDocRequestVo);

    void updateStock(Long id);

    void updateDoc(StockDocUpdateRequestVo stockDocRequestVo);
}
