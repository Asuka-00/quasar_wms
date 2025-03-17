package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.common.login.LoginUserHolder;
import com.wms.wmsend.model.entity.StockHistory;
import com.wms.wmsend.model.vo.StockHistoryResultVo;
import com.wms.wmsend.service.StockHistoryService;
import com.wms.wmsend.mapper.StockHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Ayanami
* @description 针对表【stock_history】的数据库操作Service实现
* @createDate 2025-03-05 20:14:54
*/
@Service
public class StockHistoryServiceImpl extends ServiceImpl<StockHistoryMapper, StockHistory>
    implements StockHistoryService{

    @Autowired
    private StockHistoryMapper stockHistoryMapper;

    @Override
    public void saveHistory(Long warehouseId, Long productId, Long diff, String actionType,String remark){
        if(remark.isEmpty()) return;
        StockHistory stockHistory = new StockHistory();
        stockHistory.setWarehouseId(warehouseId);
        stockHistory.setProductId(productId);
        stockHistory.setDiff(diff);
        stockHistory.setRemark(remark);
        stockHistory.setActionName(actionType);
        stockHistory.setActionUser(LoginUserHolder.getLoginUser().getUserId());
        stockHistory.setActionTime(new Date());
        this.save(stockHistory);
    }

    @Override
    public IPage<StockHistoryResultVo> listByPage(Page<StockHistory> page, String warehouseId, String productId, String actionName, String docNo) {
        return stockHistoryMapper.listByPage(page, warehouseId, productId, actionName,docNo);
    }
}




