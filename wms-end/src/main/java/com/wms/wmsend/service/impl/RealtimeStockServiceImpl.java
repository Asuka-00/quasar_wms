package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.vo.RealStockQueryVo;
import com.wms.wmsend.model.vo.RealStockResultVo;
import com.wms.wmsend.service.RealtimeStockService;
import com.wms.wmsend.mapper.RealtimeStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Ayanami
* @description 针对表【realtime_stock(实时库存表)】的数据库操作Service实现
* @createDate 2025-03-03 18:05:01
*/
@Service
public class RealtimeStockServiceImpl extends ServiceImpl<RealtimeStockMapper, RealtimeStock>
    implements RealtimeStockService{

    @Autowired
    private RealtimeStockMapper realtimeStockMapper;

    @Override
    public IPage<RealStockResultVo> listByPage(Page<RealtimeStock> page, RealStockQueryVo realStockQueryVo) {
        return realtimeStockMapper.listByPage(page, realStockQueryVo);
    }
}




