package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.StockDocItem;
import com.wms.wmsend.model.vo.StockDocDetailResultVo;
import com.wms.wmsend.service.StockDocItemService;
import com.wms.wmsend.mapper.StockDocItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【stock_doc_item(单据明细表)】的数据库操作Service实现
* @createDate 2025-03-03 17:02:07
*/
@Service
public class StockDocItemServiceImpl extends ServiceImpl<StockDocItemMapper, StockDocItem>
    implements StockDocItemService{

    @Autowired
    private StockDocItemMapper stockDocItemMapper;

    @Override
    public List<StockDocDetailResultVo> detail(Long id) {
        return stockDocItemMapper.detail(id);
    }
}




