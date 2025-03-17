package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.RealStockQueryVo;
import com.wms.wmsend.model.vo.RealStockResultVo;

/**
* @author Ayanami
* @description 针对表【realtime_stock(实时库存表)】的数据库操作Service
* @createDate 2025-03-03 18:05:01
*/
public interface RealtimeStockService extends IService<RealtimeStock> {

    IPage<RealStockResultVo> listByPage(Page<RealtimeStock> page, RealStockQueryVo realStockQueryVo);
}
