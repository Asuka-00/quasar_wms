package com.wms.wmsend.common.log;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * MyBatis SQL日志拦截器
 * 记录SQL执行内容、参数和时间
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Log4j2
public class SqlLogInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取SQL相关信息
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        
        // 获取SQL语句
        String sql = getSql(configuration, boundSql, parameter);
        
        // 执行原始方法
        Object result = invocation.proceed();
        
        // 计算执行时间
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        // 构建并记录SQL日志
        String methodName = mappedStatement.getId();
        String sqlType = mappedStatement.getSqlCommandType().name();
        int resultSize = getResultSize(result);
        
        // 记录日志
        log.info("SQL执行详情 - 方法: {}", methodName);
        log.info("SQL类型: {}", sqlType);
        log.info("SQL语句: {}", sql);
        log.info("执行时间: {} ms", executionTime);
        log.info("影响行数/返回结果数: {}", resultSize);
        
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以在这里配置拦截器的属性
    }

    /**
     * 从BoundSql获取格式化后的完整SQL语句
     */
    private String getSql(Configuration configuration, BoundSql boundSql, Object parameterObject) {
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        // 没有参数，直接返回SQL语句
        if (parameterMappings.isEmpty()) {
            return sql;
        }
        
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        
        // 如果参数不为空，则进行参数值的替换
        if (parameterObject != null) {
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                // 如果是基本类型，直接替换第一个问号
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                // 如果是复杂类型，使用反射获取属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                
                // 遍历参数映射
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    
                    // 参数值
                    Object value;
                    if (metaObject.hasGetter(propertyName)) {
                        value = metaObject.getValue(propertyName);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else {
                        // 无法获取参数值，使用默认值
                        value = null;
                    }
                    
                    // 替换问号
                    sql = sql.replaceFirst("\\?", getParameterValue(value));
                }
            }
        }
        
        return sql;
    }

    /**
     * 获取参数值的字符串表示形式
     */
    private String getParameterValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        // 根据参数类型格式化参数值
        if (value instanceof String) {
            return "'" + value + "'";
        } else if (value instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            return "'" + formatter.format(value) + "'";
        } else if (value instanceof Collection || value.getClass().isArray()) {
            return "collection";
        }
        
        return value.toString();
    }

    /**
     * 获取结果集大小
     */
    private int getResultSize(Object result) {
        if (result == null) {
            return 0;
        } else if (result instanceof Collection) {
            return ((Collection<?>) result).size();
        } else if (result instanceof Map) {
            return ((Map<?, ?>) result).size();
        } else if (result.getClass().isArray()) {
            return ((Object[]) result).length;
        } else {
            return 1; // 单个对象
        }
    }
} 