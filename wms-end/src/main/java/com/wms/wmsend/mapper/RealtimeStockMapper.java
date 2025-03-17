package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.RealStockQueryVo;
import com.wms.wmsend.model.vo.RealStockResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【realtime_stock(实时库存表)】的数据库操作Mapper
* @createDate 2025-03-03 18:05:01
* @Entity com.wms.wmsend.model.entity.RealtimeStock
*/
public interface RealtimeStockMapper extends BaseMapper<RealtimeStock> {

    IPage<RealStockResultVo> listByPage(@Param("page") Page<RealtimeStock> page,@Param("realStockQueryVo") RealStockQueryVo realStockQueryVo);
}




