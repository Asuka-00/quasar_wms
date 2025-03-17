package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.StockAlert;
import com.wms.wmsend.model.vo.StockAlertResultVo;
import com.wms.wmsend.service.StockAlertService;
import com.wms.wmsend.mapper.StockAlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【stock_alert(库存预警表)】的数据库操作Service实现
* @createDate 2025-03-04 12:48:46
*/
@Service
public class StockAlertServiceImpl extends ServiceImpl<StockAlertMapper, StockAlert>
    implements StockAlertService{

    @Autowired
    private StockAlertMapper stockAlertMapper;

    @Override
    public IPage<StockAlertResultVo> listByPage(Page<StockAlert> page, String productId, String warehouseId) {
        return stockAlertMapper.listByPage(page, productId, warehouseId);
    }
}




