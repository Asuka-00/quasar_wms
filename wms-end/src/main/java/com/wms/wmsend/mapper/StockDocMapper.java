package com.wms.wmsend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.StockDoc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.wmsend.model.vo.StockDocQueryVo;
import com.wms.wmsend.model.vo.StockResultVo;
import org.apache.ibatis.annotations.Param;

/**
* @author Ayanami
* @description 针对表【stock_doc(库存单据表)】的数据库操作Mapper
* @createDate 2025-03-03 17:08:30
* @Entity com.wms.wmsend.model.entity.StockDoc
*/
public interface StockDocMapper extends BaseMapper<StockDoc> {

    IPage<StockResultVo> listByPage(@Param("page") Page<StockDoc> page,@Param("stockDocQueryVo") StockDocQueryVo stockDocQueryVo);
}




