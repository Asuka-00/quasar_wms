package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.model.entity.InventoryDetail;
import com.wms.wmsend.model.vo.InventoryDetailResultVo;
import com.wms.wmsend.service.InventoryDetailService;
import com.wms.wmsend.mapper.InventoryDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ayanami
* @description 针对表【inventory_detail(盘点明细表)】的数据库操作Service实现
* @createDate 2025-03-05 16:50:12
*/
@Service
public class InventoryDetailServiceImpl extends ServiceImpl<InventoryDetailMapper, InventoryDetail>
    implements InventoryDetailService{

    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;

    @Override
    public List<InventoryDetailResultVo> listById(Long id) {
        return inventoryDetailMapper.selectPrintList(id);
    }
}




