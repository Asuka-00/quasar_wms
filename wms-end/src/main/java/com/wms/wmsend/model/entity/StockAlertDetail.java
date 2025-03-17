package com.wms.wmsend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName stock_alert_detail
 */
@TableName(value ="stock_alert_detail")
@Data
public class StockAlertDetail {

    @TableId(value = "id", type= IdType.AUTO)
    private Long id;

    /**
     * 消息
     */
    private String msg;

    /**
     * 通知对象
     */
    private Long sendTo;

    /**
     * 是否已读(0未读，1已读)
     */
    private Long status;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @TableField(value = "created_by",fill = FieldFill.INSERT)
    private String createdBy;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @TableField(value = "created_time",fill = FieldFill.INSERT)
    private Date createdTime;

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
        StockAlertDetail other = (StockAlertDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getMsg() == null ? other.getMsg() == null : this.getMsg().equals(other.getMsg()))
            && (this.getSendTo() == null ? other.getSendTo() == null : this.getSendTo().equals(other.getSendTo()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getMsg() == null) ? 0 : getMsg().hashCode());
        result = prime * result + ((getSendTo() == null) ? 0 : getSendTo().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", msg=").append(msg);
        sb.append(", sendTo=").append(sendTo);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}