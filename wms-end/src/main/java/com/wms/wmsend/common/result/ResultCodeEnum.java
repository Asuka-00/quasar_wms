package com.wms.wmsend.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARAM_ERROR(202, "参数不正确"),
    SERVICE_ERROR(203, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    DELETE_ERROR(207, "该数据被引用,请先删除子集"),
    CODE_EXPIRED(208,"验证码过期"),
    CODE_ERROR(209,"验证码错误"),
    USERNAME_NULL(210,"用户名不存在"),
    USERNAME_EXIST(216,"用户名已存在"),
    PASSWORD_ERROR(211,"密码错误"),
    LOGIN_AUTH(212,"未登录"),
    STOCK_NOT_ENOUGH(213,"库存不足"),
    STOCK_DOC_CANNOT_CANCEL(214,"单据状态不允许取消"),
    INVENTORY_STATUS_ERROR(215,"盘点状态不允许取消"),
    STOCK_DOC_TYPE_ERROR(216,"单据类型错误"),
    WAREHOUSE_NAME_EXIST(217,"仓库名称已存在"),
    CATEGORY_NAME_EXIST(218,"分类名称已存在"),
    PRODUCT_EXIST(219,"商品已存在"),
    DEPT_EXIST(220,"部门已存在"),
    UNIT_NAME_EXIST(221,"单位已存在"),
    USER_NOT_EXIST(222,"用户不存在"),
    PRODUCT_USED_STOCK_DOC(223,"商品已被入库单据引用"),
    PRODUCT_USED_WAREHOUSE(224,"商品已被库存引用"),
    WAREHOUSE_USED_STOCK(225,"仓库已被引用"),
    PRODUCT_TYPE_ERROR(226,"商品分类已被商品引用"),
    STOCK_NOT_EXIST(227,"库存不存在"),
    WAREHOUSE_USED_PRODUCT(228,"仓库已被商品引用"),

    TOKEN_EXPIRED(401, "token过期"),
    TOKEN_INVALID(402, "token非法");;
    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
