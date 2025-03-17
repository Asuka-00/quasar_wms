package com.wms.wmsend.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * API日志切面，记录接口的入参和返回值
 */
@Aspect
@Component
@Log4j2
public class ApiLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定义切点Pointcut - 所有controller包下的类方法
     */
    @Pointcut("execution(* com.wms.wmsend.controller..*.*(..))")
    public void controllerPointcut() {}

    /**
     * 环绕通知，记录接口调用的入参和返回值
     */
    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取请求信息
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String remoteAddr = getRemoteAddr(request);
        
        // 获取方法参数名和参数值
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = proceedingJoinPoint.getArgs();
        
        // 构建参数Map
        Map<String, Object> paramsMap = buildParamsMap(paramNames, paramValues);
        
        // 记录请求日志
        log.info("=== 接口请求开始 ===");
        log.info("请求URI: {}", requestURI);
        log.info("请求方法: {}", requestMethod);
        log.info("请求IP: {}", remoteAddr);
        log.info("请求类方法: {}.{}", signature.getDeclaringTypeName(), method.getName());
        log.info("请求参数: {}", objectMapper.writeValueAsString(paramsMap));
        
        // 执行目标方法
        Object result = proceedingJoinPoint.proceed();
        
        // 记录响应结果
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("响应结果: {}", result != null ? objectMapper.writeValueAsString(result) : "null");
        log.info("执行耗时: {} ms", elapsedTime);
        log.info("=== 接口请求结束 ===");
        
        return result;
    }

    /**
     * 异常通知，记录接口调用过程中的异常信息
     */
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        log.error("=== 接口异常 ===");
        log.error("异常类方法: {}.{}", signature.getDeclaringTypeName(), method.getName());
        log.error("异常信息: {}", e.getMessage());
        log.error("异常堆栈: ", e);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    /**
     * 构建参数Map，过滤掉HttpServletRequest、HttpServletResponse和MultipartFile等类型
     */
    private Map<String, Object> buildParamsMap(String[] paramNames, Object[] paramValues) {
        Map<String, Object> paramsMap = new HashMap<>();
        if (paramNames != null && paramValues != null) {
            for (int i = 0; i < paramNames.length; i++) {
                Object value = paramValues[i];
                // 过滤掉HttpServletRequest、HttpServletResponse、MultipartFile等类型
                if (value instanceof HttpServletRequest
                        || value instanceof HttpServletResponse
                        || value instanceof MultipartFile
                        || value instanceof MultipartFile[]) {
                    continue;
                }
                paramsMap.put(paramNames[i], value);
            }
        }
        return paramsMap;
    }
} 