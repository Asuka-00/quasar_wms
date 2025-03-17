package com.wms.wmsend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 库存预警表
 * @TableName stock_alert
 */
@TableName(value ="stock_alert")
@Data
public class StockAlert extends BaseEntity {
    /**
     * 商品类别ID
     */
    private Long categoryId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 最低库存阈值
     */
    private Integer minStock;

    /**
     * 最高库存阈值
     */
    private Integer maxStock;

    /**
     * 预警方式(phone/email)
     */
    private String sendType;

    /**
     * 发送对象(用户ID)
     */
    private Long sendTo;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StockAlert other = (StockAlert) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getWarehouseId() == null ? other.getWarehouseId() == null : this.getWarehouseId().equals(other.getWarehouseId()))
            && (this.getMinStock() == null ? other.getMinStock() == null : this.getMinStock().equals(other.getMinStock()))
            && (this.getMaxStock() == null ? other.getMaxStock() == null : this.getMaxStock().equals(other.getMaxStock()))
            && (this.getSendType() == null ? other.getSendType() == null : this.getSendType().equals(other.getSendType()))
            && (this.getSendTo() == null ? other.getSendTo() == null : this.getSendTo().equals(other.getSendTo()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getUpdatedBy() == null ? other.getUpdatedBy() == null : this.getUpdatedBy().equals(other.getUpdatedBy()))
            && (this.getUpdatedTime() == null ? other.getUpdatedTime() == null : this.getUpdatedTime().equals(other.getUpdatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getWarehouseId() == null) ? 0 : getWarehouseId().hashCode());
        result = prime * result + ((getMinStock() == null) ? 0 : getMinStock().hashCode());
        result = prime * result + ((getMaxStock() == null) ? 0 : getMaxStock().hashCode());
        result = prime * result + ((getSendType() == null) ? 0 : getSendType().hashCode());
        result = prime * result + ((getSendTo() == null) ? 0 : getSendTo().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getUpdatedBy() == null) ? 0 : getUpdatedBy().hashCode());
        result = prime * result + ((getUpdatedTime() == null) ? 0 : getUpdatedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", productId=").append(productId);
        sb.append(", warehouseId=").append(warehouseId);
        sb.append(", minStock=").append(minStock);
        sb.append(", maxStock=").append(maxStock);
        sb.append(", sendType=").append(sendType);
        sb.append(", sendTo=").append(sendTo);
        sb.append("]");
        return sb.toString();
    }
}