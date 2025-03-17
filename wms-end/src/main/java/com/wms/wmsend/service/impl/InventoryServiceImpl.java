package com.wms.wmsend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.TextAlignment;
import com.wms.wmsend.common.minio.MinioProperties;
import com.wms.wmsend.common.result.ResultCodeEnum;
import com.wms.wmsend.common.util.FreeMarkerUtil;
import com.wms.wmsend.customer.exception.WmsException;
import com.wms.wmsend.mapper.InventoryDetailMapper;
import com.wms.wmsend.model.entity.Inventory;
import com.wms.wmsend.model.entity.InventoryDetail;
import com.wms.wmsend.model.entity.RealtimeStock;
import com.wms.wmsend.model.enums.InventoryStatusEnum;
import com.wms.wmsend.model.vo.*;
import com.wms.wmsend.service.InventoryService;
import com.wms.wmsend.mapper.InventoryMapper;
import com.wms.wmsend.service.RealtimeStockService;
import com.wms.wmsend.service.StockHistoryService;
import freemarker.template.TemplateException;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ayanami
 * @description 针对表【inventory(盘点任务表)】的数据库操作Service实现
 * @createDate 2025-03-04 13:03:29
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory>
        implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private RealtimeStockService realtimeStockService;

    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;

    @Autowired
    private MinioProperties properties;

    @Autowired
    private MinioClient client;

    @Autowired
    private StockHistoryService stockHistoryService;

    @Override
    public IPage<InventoryResultVo> listByPage(Page<Inventory> page, Long warehouseId, String inventoryNo) {
        return inventoryMapper.listByPage(page, warehouseId, inventoryNo);
    }

    @Override
    public void saveInventory(InventoryRequestVo inventoryRequestVo) {
        //保存盘点主单
        Inventory inventory = new Inventory();
        inventory.setInventoryNo(inventoryRequestVo.getInventoryNo());
        inventory.setWarehouseId(inventoryRequestVo.getWarehouseId());
        inventory.setStatus(InventoryStatusEnum.RUNNING.getCode());
        inventory.setRemark(inventoryRequestVo.getRemark());
        inventoryMapper.insert(inventory);

        //保存盘点明细
        Long warehouseId = inventoryRequestVo.getWarehouseId();
        inventoryRequestVo.getItems().forEach(item -> {
            //获取对应产品的实际库存
            LambdaQueryWrapper<RealtimeStock> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RealtimeStock::getWarehouseId, warehouseId);
            queryWrapper.eq(RealtimeStock::getProductId, item.getProductId());
            RealtimeStock realtimeStock = realtimeStockService.getOne(queryWrapper);
            if (realtimeStock == null) {
                throw new WmsException(ResultCodeEnum.STOCK_NOT_EXIST);
            }
            //保存盘点明细
            InventoryDetail detail = new InventoryDetail();
            detail.setInventoryId(inventory.getId());
            detail.setProductId(item.getProductId());
            detail.setSystemQuantity(realtimeStock.getQuantity());
            inventoryDetailMapper.insert(detail);
        });
    }

    @Override
    public void updateInventory(InventoryUpdateRequestVo inventoryRequestVo) {
        //更新盘点主单
        Inventory inventory = new Inventory();
        inventory.setId(inventoryRequestVo.getId());
        inventory.setInventoryNo(inventoryRequestVo.getInventoryNo());
        inventory.setWarehouseId(inventoryRequestVo.getWarehouseId());
        inventory.setStatus(InventoryStatusEnum.RUNNING.getCode());
        inventory.setRemark(inventoryRequestVo.getRemark());
        inventoryMapper.updateById(inventory);

        //删除原有盘点明细
        LambdaQueryWrapper<InventoryDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryDetail::getInventoryId, inventory.getId());
        inventoryDetailMapper.delete(queryWrapper);

        //保存盘点明细
        Long warehouseId = inventoryRequestVo.getWarehouseId();
        inventoryRequestVo.getItems().forEach(item -> {
            //获取对应产品的实际库存
            LambdaQueryWrapper<RealtimeStock> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(RealtimeStock::getWarehouseId, warehouseId);
            queryWrapper1.eq(RealtimeStock::getProductId, item.getProductId());
            RealtimeStock realtimeStock = realtimeStockService.getOne(queryWrapper1);
            if (realtimeStock == null) {
                throw new WmsException(ResultCodeEnum.STOCK_NOT_EXIST);
            }
            //保存盘点明细
            InventoryDetail detail = new InventoryDetail();
            detail.setInventoryId(inventory.getId());
            detail.setProductId(item.getProductId());
            detail.setSystemQuantity(realtimeStock.getQuantity());
            inventoryDetailMapper.insert(detail);
        });
    }

    @Override
    public void inventory(InventoryUpdateRequestVo inventoryRequestVo) {
        //更新盘点主单状态
        Inventory inventory = new Inventory();
        inventory.setId(inventoryRequestVo.getId());
        inventory.setStatus(InventoryStatusEnum.APPROVED.getCode());
        inventoryMapper.updateById(inventory);

        //更新盘点明细
        inventoryRequestVo.getItems().forEach(item -> {
            InventoryDetail detail = new InventoryDetail();
            detail.setId(item.getId());
            detail.setActualQuantity(item.getActualQuantity());
            inventoryDetailMapper.updateById(detail);
        });
    }

    @Override
    public void complete(Long id) {
        //更新盘点主单状态
        Inventory inventory = new Inventory();
        inventory.setId(id);
        inventory.setStatus(InventoryStatusEnum.COMPLETED.getCode());
        inventoryMapper.updateById(inventory);

        //获取盘点主单
        Inventory inventory1 = inventoryMapper.selectById(id);
        if (inventory1 == null) {
            throw new WmsException(ResultCodeEnum.DATA_ERROR);
        }

        //根据盘点明细更新实时库存
        LambdaQueryWrapper<InventoryDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryDetail::getInventoryId, id);
        inventoryDetailMapper.selectList(queryWrapper).forEach(detail -> {
            LambdaUpdateWrapper<RealtimeStock> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(RealtimeStock::getWarehouseId, inventory1.getWarehouseId());
            updateWrapper.eq(RealtimeStock::getProductId, detail.getProductId());
            updateWrapper.setSql("quantity = quantity + " + (detail.getActualQuantity() - detail.getSystemQuantity()));
            realtimeStockService.update(updateWrapper);
            //保存库存变更历史
            stockHistoryService.saveHistory(inventory1.getWarehouseId(), detail.getProductId(),
                    (long) (detail.getActualQuantity() - detail.getSystemQuantity()), "INV",  inventory1.getInventoryNo());
        });
    }

    @Override
    public String print(Long id) throws IOException, TemplateException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //打印数据源
        Map<String, Object> dataSource = new HashMap<>();
        //获取盘点主单
        InventoryPrintVo inventory = inventoryMapper.selectPrintData(id);
        if (inventory == null) {
            throw new WmsException(ResultCodeEnum.DATA_ERROR);
        }
        //状态转换
        inventory.setStatusDesc(InventoryStatusEnum.getDescByCode(inventory.getStatus()));
        dataSource.put("inventory", inventory);
        dataSource.put("createdName", inventory.getCreatedName());
        dataSource.put("createdTime", inventory.getCreatedTime());
        //获取盘点明细
        List<InventoryDetailResultVo> details = inventoryDetailMapper.selectPrintList(id);

        //添加盘点结果
        details.forEach(i->{
            if (i.getDiff()==null)return;

            if(i.getDiff()>0) i.setStatus("盘盈");
            else if(i.getDiff()<0) i.setStatus("盘亏");
            else i.setStatus("正常");
        });

        dataSource.put("details", details);

        //将null转换为""
        FreeMarkerUtil.nullToEmpty(dataSource);

        //打印
        // 生成html
        String htmlContent = FreeMarkerUtil.generateHtml("inventory.ftl", dataSource);
        // 转为pdf
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(pdfStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());  // 设置为 A4 横向页
        document.setTextAlignment(TextAlignment.CENTER);  // 设置为居中

        // 字体选择
        FontProvider fontProvider = new FontProvider();
        String fontPath = ResourceUtils.getFile("classpath:font/NotoSansSC-VariableFont_wght.ttf").getAbsolutePath();
        fontProvider.addFont(fontPath);
        if (fontProvider.getFontSet().getFonts().isEmpty()) {
            throw new RuntimeException("Font Provider contains zero fonts. At least one font shall be present.");
        }

        // 转换 HTML 为 PDF
        HtmlConverter.convertToPdf(htmlContent, pdfDoc, new com.itextpdf.html2pdf.ConverterProperties().setFontProvider(fontProvider));

        // 保存文件到 Minio
        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
        if (!bucketExists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(properties.getBucketName()).config(createBucketPolicyConfig(properties.getBucketName())).build());
        }


        // 手动设置 Content-Disposition 为 inline
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Disposition", "inline");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "inventory-" + sdf.format(new Date()) + ".pdf";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfStream.toByteArray());
        client.putObject(PutObjectArgs.builder()
                .bucket(properties.getBucketName())
                .object(fileName)
                .stream(inputStream, pdfStream.size(), -1)
                .contentType("application/pdf")
                .headers(headers)  // 设置为 inline 以便浏览器预览
                .build());

        return String.join("/", properties.getEndpoint(), properties.getBucketName(), fileName);
    }

    private String createBucketPolicyConfig(String bucketName) {

        return String.format(
                "{\n" +
                "  \"Statement\" : [ {\n" +
                "    \"Action\" : \"s3:GetObject\",\n" +
                "    \"Effect\" : \"Allow\",\n" +
                "    \"Principal\" : \"*\",\n" +
                "    \"Resource\" : \"arn:aws:s3:::%s/*\"\n" +
                "  } ],\n" +
                "  \"Version\" : \"2012-10-17\"\n" +
                "}", bucketName);
    }

}




