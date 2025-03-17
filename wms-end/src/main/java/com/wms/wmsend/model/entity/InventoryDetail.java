package com.wms.wmsend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 盘点明细表
 * @TableName inventory_detail
 */
@TableName(value ="inventory_detail")
@Data
public class InventoryDetail extends BaseEntity {
    /**
     * 盘点任务ID
     */
    private Long inventoryId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 实际盘点数量
     */
    private Integer actualQuantity;

    /**
     * 系统库存数量
     */
    private Integer systemQuantity;

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
        InventoryDetail other = (InventoryDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getInventoryId() == null ? other.getInventoryId() == null : this.getInventoryId().equals(other.getInventoryId()))
            && (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getActualQuantity() == null ? other.getActualQuantity() == null : this.getActualQuantity().equals(other.getActualQuantity()))
            && (this.getSystemQuantity() == null ? other.getSystemQuantity() == null : this.getSystemQuantity().equals(other.getSystemQuantity()))
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
        result = prime * result + ((getInventoryId() == null) ? 0 : getInventoryId().hashCode());
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getActualQuantity() == null) ? 0 : getActualQuantity().hashCode());
        result = prime * result + ((getSystemQuantity() == null) ? 0 : getSystemQuantity().hashCode());
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
        sb.append(", inventoryId=").append(inventoryId);
        sb.append(", productId=").append(productId);
        sb.append(", actualQuantity=").append(actualQuantity);
        sb.append(", systemQuantity=").append(systemQuantity);
        sb.append("]");
        return sb.toString();
    }
}