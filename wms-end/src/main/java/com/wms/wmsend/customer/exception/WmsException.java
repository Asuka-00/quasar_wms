package com.wms.wmsend.customer.exception;

import com.wms.wmsend.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class WmsException extends RuntimeException {

    //异常状态码
    private Integer code;
    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public WmsException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 根据响应结果枚举对象创建异常对象
     * @param resultCodeEnum
     */
    public WmsException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "WmsException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
