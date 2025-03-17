package com.wms.wmsend.customer.config;

import com.wms.wmsend.common.log.SqlLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis配置类
 */
@Configuration
public class MyBatisConfig {

    /**
     * 注册SQL日志拦截器
     */
    @Bean
    public SqlLogInterceptor sqlLogInterceptor() {
        SqlLogInterceptor interceptor = new SqlLogInterceptor();
        Properties properties = new Properties();
        // 可以在这里添加配置属性
        interceptor.setProperties(properties);
        return interceptor;
    }
} 