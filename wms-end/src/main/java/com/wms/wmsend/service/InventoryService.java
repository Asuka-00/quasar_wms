package com.wms.wmsend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.model.entity.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.wmsend.model.vo.InventoryRequestVo;
import com.wms.wmsend.model.vo.InventoryResultVo;
import com.wms.wmsend.model.vo.InventoryUpdateRequestVo;
import freemarker.template.TemplateException;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
* @author Ayanami
* @description 针对表【inventory(盘点任务表)】的数据库操作Service
* @createDate 2025-03-04 13:03:29
*/
public interface InventoryService extends IService<Inventory> {

    IPage<InventoryResultVo> listByPage(Page<Inventory> page, Long warehouseId, String inventoryNo);

    void saveInventory(InventoryRequestVo inventoryRequestVo);

    void updateInventory(InventoryUpdateRequestVo inventoryRequestVo);

    void inventory(InventoryUpdateRequestVo inventoryRequestVo);

    void complete(Long id);

    String print(Long id) throws IOException, TemplateException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
