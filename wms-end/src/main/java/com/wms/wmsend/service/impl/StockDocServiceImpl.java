package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.mapper.RealtimeStockMapper;
import com.wms.wmsend.mapper.StockDocItemMapper;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.entity.StockDoc;
import com.wms.wmsend.model.entity.StockDocItem;
import com.wms.wmsend.model.entity.StockHistory;
import com.wms.wmsend.model.vo.*;
import com.wms.wmsend.service.RealtimeStockService;
import com.wms.wmsend.service.StockDocService;
import com.wms.wmsend.mapper.StockDocMapper;
import com.wms.wmsend.service.StockHistoryService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Ayanami
* @description 针对表【stock_doc(库存单据表)】的数据库操作Service实现
* @createDate 2025-03-03 17:08:30
*/
@Service
public class StockDocServiceImpl extends ServiceImpl<StockDocMapper, StockDoc>
    implements StockDocService{

    @Autowired
    private StockDocMapper stockDocMapper;

    @Autowired
    private StockDocItemMapper stockDocItemMapper;

    @Autowired
    private RealtimeStockService realtimeStockService;

    @Autowired
    private StockHistoryService stockHistoryService;

    @Override
    public IPage<StockResultVo> listByPage(Page<StockDoc> page, StockDocQueryVo stockDocQueryVo) {
        return stockDocMapper.listByPage(page, stockDocQueryVo);
    }

    @Override
    public void saveDoc(StockDocRequestVo stockDocRequestVo) {
        //保存主单据
        StockDoc stockDoc = new StockDoc();
        stockDoc.setDocumentNo(stockDocRequestVo.getDocumentNo());
        stockDoc.setDocumentType(stockDocRequestVo.getDocumentType());
        stockDoc.setWarehouseId(stockDocRequestVo.getWarehouseId());
        stockDoc.setStatus(stockDocRequestVo.getStatus());
        stockDoc.setOperatorUser(stockDocRequestVo.getOperatorUser());
        stockDoc.setRemark(stockDocRequestVo.getRemark());
        stockDocMapper.insert(stockDoc);

        //保存明细
        stockDocRequestVo.getItems().forEach(item -> {
            //库存是否足够
            if ("OUT".equals(stockDocRequestVo.getDocumentType())) {
                RealtimeStock realtimeStock = realtimeStockService.getOne(new LambdaQueryWrapper<RealtimeStock>()
                        .eq(RealtimeStock::getProductId, item.getProductId())
                        .eq(RealtimeStock::getWarehouseId, stockDoc.getWarehouseId()));
                if (realtimeStock == null || realtimeStock.getQuantity() < item.getQuantity()) {
                    throw new WmsException(ResultCodeEnum.STOCK_NOT_ENOUGH);
                }
            }

            StockDocItem stockDocItem = new StockDocItem();
            stockDocItem.setDocumentId(stockDoc.getId());
            stockDocItem.setProductId(item.getProductId());
            stockDocItem.setQuantity(item.getQuantity());
            stockDocItemMapper.insert(stockDocItem);
        });
    }

    @Override
    public void updateStock(Long id) {
        //更新库存
        StockDoc stockDoc = stockDocMapper.selectById(id);

        LambdaQueryWrapper<StockDocItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockDocItem::getDocumentId, id);
        List<StockDocItem> items = stockDocItemMapper.selectList(queryWrapper);

        //入库还是出库
        if ("IN".equals(stockDoc.getDocumentType())) {
            //入库
            items.forEach(item -> {
                //查询库存
                RealtimeStock realtimeStock = realtimeStockService.getOne(new LambdaQueryWrapper<RealtimeStock>()
                        .eq(RealtimeStock::getProductId, item.getProductId())
                        .eq(RealtimeStock::getWarehouseId, stockDoc.getWarehouseId()));
                if (realtimeStock == null) {
                    //新增库存
                    realtimeStock = new RealtimeStock();
                    realtimeStock.setProductId(item.getProductId());
                    realtimeStock.setWarehouseId(stockDoc.getWarehouseId());
                    realtimeStock.setQuantity(item.getQuantity());
                    realtimeStockService.save(realtimeStock);
                    StockHistory stockHistory = new StockHistory();
                    stockHistory.setWarehouseId(stockDoc.getWarehouseId());
                    stockHistory.setProductId(item.getProductId());
                    stockHistory.setDiff(Long.valueOf(item.getQuantity()));
                    stockHistory.setActionName("IN");
                    stockHistoryService.save(stockHistory);
                    //保存历史
                    stockHistoryService.saveHistory(stockDoc.getWarehouseId(), item.getProductId(), Long.valueOf(item.getQuantity()),
                            "IN",stockDoc.getDocumentNo());
                } else {
                    //更新库存
                    realtimeStock.setQuantity(realtimeStock.getQuantity() + item.getQuantity());
                    realtimeStockService.updateById(realtimeStock);

                    //保存历史
                    stockHistoryService.saveHistory(stockDoc.getWarehouseId(), item.getProductId(), Long.valueOf(item.getQuantity()),
                            "IN",stockDoc.getDocumentNo());
                }
            });
        } else {
            //出库
            items.forEach(item -> {
                //查询库存
                RealtimeStock realtimeStock = realtimeStockService.getOne(new LambdaQueryWrapper<RealtimeStock>()
                        .eq(RealtimeStock::getProductId, item.getProductId())
                        .eq(RealtimeStock::getWarehouseId, stockDoc.getWarehouseId()));
                if (realtimeStock == null) {
                    //没有库存
                    throw new WmsException(ResultCodeEnum.STOCK_NOT_ENOUGH);
                } else {
                    if (realtimeStock.getQuantity() < item.getQuantity()) {
                        //库存不足
                        throw new WmsException(ResultCodeEnum.STOCK_NOT_ENOUGH);
                    }
                    //更新库存
                    realtimeStock.setQuantity(realtimeStock.getQuantity() - item.getQuantity());
                    realtimeStockService.updateById(realtimeStock);
                    //保存历史
                    stockHistoryService.saveHistory(stockDoc.getWarehouseId(), item.getProductId(), Long.valueOf(item.getQuantity()),
                            "OUT",stockDoc.getDocumentNo());
                }
            });
        }
    }

    @Override
    public void updateDoc(StockDocUpdateRequestVo stockDocRequestVo) {
        //更新主单据
        StockDoc stockDoc = new StockDoc();
        stockDoc.setId(stockDocRequestVo.getId());
        stockDoc.setDocumentNo(stockDocRequestVo.getDocumentNo());
        stockDoc.setDocumentType(stockDocRequestVo.getDocumentType());
        stockDoc.setWarehouseId(stockDocRequestVo.getWarehouseId());
        stockDoc.setStatus(stockDocRequestVo.getStatus());
        stockDoc.setOperatorUser(stockDocRequestVo.getOperatorUser());
        stockDoc.setRemark(stockDocRequestVo.getRemark());
        stockDocMapper.updateById(stockDoc);

        //删除原有明细
        LambdaQueryWrapper<StockDocItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockDocItem::getDocumentId, stockDoc.getId());
        stockDocItemMapper.delete(queryWrapper);

        //保存新明细
        stockDocRequestVo.getItems().forEach(item -> {
            //库存是否足够
            if("OUT".equals(stockDocRequestVo.getDocumentType())){
                RealtimeStock realtimeStock = realtimeStockService.getOne(new LambdaQueryWrapper<RealtimeStock>()
                        .eq(RealtimeStock::getProductId, item.getProductId())
                        .eq(RealtimeStock::getWarehouseId, stockDoc.getWarehouseId()));
                if(realtimeStock == null || realtimeStock.getQuantity() < item.getQuantity()){
                    throw new WmsException(ResultCodeEnum.STOCK_NOT_ENOUGH);
                }
            }

            StockDocItem stockDocItem = new StockDocItem();
            stockDocItem.setDocumentId(stockDoc.getId());
            stockDocItem.setProductId(item.getProductId());
            stockDocItem.setQuantity(item.getQuantity());
            stockDocItemMapper.insert(stockDocItem);
        });
    }
}




