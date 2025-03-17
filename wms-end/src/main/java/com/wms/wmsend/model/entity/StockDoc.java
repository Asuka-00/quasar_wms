package com.wms.wmsend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 库存单据表
 * @TableName stock_doc
 */
@TableName(value ="stock_doc")
@Data
public class StockDoc extends BaseEntity {
    /**
     * 单据编号
     */
    private String documentNo;

    /**
     * 类型(IN/OUT)
     */
    private String documentType;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 单据状态(进行中/已审批/已完成)
     */
    private Long status;

    /**
     * 审批人
     */
    private Long operatorUser;

    /**
     * 备注
     */
    private String remark;

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
        StockDoc other = (StockDoc) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDocumentNo() == null ? other.getDocumentNo() == null : this.getDocumentNo().equals(other.getDocumentNo()))
            && (this.getDocumentType() == null ? other.getDocumentType() == null : this.getDocumentType().equals(other.getDocumentType()))
            && (this.getWarehouseId() == null ? other.getWarehouseId() == null : this.getWarehouseId().equals(other.getWarehouseId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getOperatorUser() == null ? other.getOperatorUser() == null : this.getOperatorUser().equals(other.getOperatorUser()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
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
        result = prime * result + ((getDocumentNo() == null) ? 0 : getDocumentNo().hashCode());
        result = prime * result + ((getDocumentType() == null) ? 0 : getDocumentType().hashCode());
        result = prime * result + ((getWarehouseId() == null) ? 0 : getWarehouseId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getOperatorUser() == null) ? 0 : getOperatorUser().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
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
        sb.append(", documentNo=").append(documentNo);
        sb.append(", documentType=").append(documentType);
        sb.append(", warehouseId=").append(warehouseId);
        sb.append(", status=").append(status);
        sb.append(", operatorUser=").append(operatorUser);
        sb.append(", remark=").append(remark);
        sb.append("]");
        return sb.toString();
    }
}