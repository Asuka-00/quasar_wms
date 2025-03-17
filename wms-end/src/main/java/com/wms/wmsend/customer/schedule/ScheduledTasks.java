package com.wms.wmsend.customer.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.entity.StockAlert;
import com.wms.wmsend.model.entity.StockAlertDetail;
import com.wms.wmsend.model.entity.SysUser;
import com.wms.wmsend.model.vo.StockAlertResultVo;
import com.wms.wmsend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {


    @Autowired
    private StockAlertService stockAlertService;

    @Autowired
    private RealtimeStockService realtimeStockService;


    @Autowired
    private StockAlertDetailService stockAlertDetailService;
    /**
     * 每半小时检查一次仓库库存
     */
    @Scheduled(initialDelay = 1000, fixedRate = 1000 * 60 * 30)
    public void checkWarehouseStock() {
        //查询预警规则
        Page<StockAlert> page = new Page<>(1,10000);
        List<StockAlertResultVo> list = stockAlertService.listByPage(page,"","").getRecords();

        //获取通知表
        List<StockAlertDetail> stockAlertDetails = stockAlertDetailService.list();

        list.forEach(item->{
            int minStock = item.getMinStock();
            int maxStock = item.getMaxStock();
            String type = item.getSendType();
            String content;

            //查询实时库存
            LambdaQueryWrapper<RealtimeStock> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RealtimeStock::getProductId,item.getProductId());
            queryWrapper.eq(RealtimeStock::getWarehouseId,item.getWarehouseId());
            RealtimeStock realtimeStock = realtimeStockService.getOne(queryWrapper);
            if (realtimeStock==null) return;

            int currentStock = realtimeStock.getQuantity();
            if(currentStock>maxStock){
                content = "仓库："+item.getWarehouseName()+" 商品："+item.getProductName()+" 当前库存："+currentStock+" 高于预警值："+maxStock;
            } else if (currentStock<minStock){
                content = "仓库："+item.getWarehouseName()+" 商品："+item.getProductName()+" 当前库存："+currentStock+" 低于预警值："+minStock;
            } else {
                content = null;
            }

            int count = stockAlertDetails.stream().filter(detail -> detail.getMsg().equalsIgnoreCase(content)).toArray().length;

            if (content != null && count==0) {
                //插入通知表
                StockAlertDetail stockAlertDetail = new StockAlertDetail();
                stockAlertDetail.setStatus(0L);
                stockAlertDetail.setMsg(content);
                stockAlertDetail.setSendTo(Long.valueOf(item.getSendTo()));
                stockAlertDetailService.save(stockAlertDetail);

            }

        });
    }
}
