package com.wms.wmsend.customer.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wms.wmsend.common.login.LoginUserHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createdBy", String.class, LoginUserHolder.getLoginUser()==null?"system":LoginUserHolder.getLoginUser().getUsername());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updatedBy", String.class, LoginUserHolder.getLoginUser().getUsername());
    }
}
