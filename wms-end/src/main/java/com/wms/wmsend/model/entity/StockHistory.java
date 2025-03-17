package com.wms.wmsend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName stock_history
 */
@TableName(value ="stock_history")
@Data
public class StockHistory {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 变更数量
     */
    private Long diff;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作方式
     */
    private String actionName;

    /**
     * 操作人
     */
    private Long actionUser;

    /**
     * 操作时间
     */
    private Date actionTime;

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
        StockHistory other = (StockHistory) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getWarehouseId() == null ? other.getWarehouseId() == null : this.getWarehouseId().equals(other.getWarehouseId()))
            && (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getDiff() == null ? other.getDiff() == null : this.getDiff().equals(other.getDiff()))
            && (this.getActionName() == null ? other.getActionName() == null : this.getActionName().equals(other.getActionName()))
            && (this.getActionUser() == null ? other.getActionUser() == null : this.getActionUser().equals(other.getActionUser()))
            && (this.getActionTime() == null ? other.getActionTime() == null : this.getActionTime().equals(other.getActionTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getWarehouseId() == null) ? 0 : getWarehouseId().hashCode());
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getDiff() == null) ? 0 : getDiff().hashCode());
        result = prime * result + ((getActionName() == null) ? 0 : getActionName().hashCode());
        result = prime * result + ((getActionUser() == null) ? 0 : getActionUser().hashCode());
        result = prime * result + ((getActionTime() == null) ? 0 : getActionTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", warehouseId=").append(warehouseId);
        sb.append(", productId=").append(productId);
        sb.append(", diff=").append(diff);
        sb.append(", actionName=").append(actionName);
        sb.append(", actionUser=").append(actionUser);
        sb.append(", actionTime=").append(actionTime);
        sb.append("]");
        return sb.toString();
    }
}