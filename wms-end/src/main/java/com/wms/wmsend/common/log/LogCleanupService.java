package com.wms.wmsend.common.log;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志清理服务，启动时自动清理7天前的日志文件
 */
@Component
@Log4j2
public class LogCleanupService implements CommandLineRunner {

    // 日志文件目录
    private static final String LOG_DIR = "logs/backup";
    // 日志保留天数
    private static final int RETENTION_DAYS = 7;
    // 日志文件名中的日期正则表达式
    private static final Pattern DATE_PATTERN = Pattern.compile("-(\\d{4}-\\d{2}-\\d{2})-");
    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 应用启动时执行，清理过期日志
     */
    @Override
    public void run(String... args) {
        cleanupLogs();
    }

    /**
     * 每天凌晨2点定时清理过期日志
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledCleanup() {
        cleanupLogs();
    }

    /**
     * 清理过期日志文件
     */
    private void cleanupLogs() {
        log.info("开始清理超过{}天的日志文件...", RETENTION_DAYS);
        
        File logDirectory = new File(LOG_DIR);
        // 如果日志目录不存在，则创建
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
            log.info("日志目录不存在，已创建: {}", logDirectory.getAbsolutePath());
            return;
        }
        
        // 获取截止日期（当前日期减去保留天数）
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RETENTION_DAYS);
        log.info("将清理{}之前的日志文件", cutoffDate.format(DATE_FORMATTER));
        
        // 获取目录中的所有文件
        File[] files = logDirectory.listFiles();
        if (files == null || files.length == 0) {
            log.info("没有找到需要清理的日志文件");
            return;
        }
        
        // 遍历文件，删除超过保留期的文件
        Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> isLogFileExpired(file, cutoffDate))
                .forEach(file -> {
                    try {
                        Path path = Paths.get(file.getAbsolutePath());
                        Files.delete(path);
                        log.info("已删除过期日志文件: {}", file.getName());
                    } catch (Exception e) {
                        log.error("删除日志文件失败: {}", file.getName(), e);
                    }
                });
        
        log.info("日志清理完成");
    }

    /**
     * 判断日志文件是否已过期
     */
    private boolean isLogFileExpired(File file, LocalDateTime cutoffDate) {
        String fileName = file.getName();
        Matcher matcher = DATE_PATTERN.matcher(fileName);
        
        // 如果文件名中包含日期
        if (matcher.find()) {
            String dateStr = matcher.group(1);
            try {
                LocalDateTime fileDate = LocalDateTime.parse(dateStr + " 00:00:00", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return fileDate.isBefore(cutoffDate);
            } catch (Exception e) {
                log.warn("解析日志文件日期失败: {}", fileName, e);
            }
        }
        
        // 如果无法从文件名解析日期，则使用文件的最后修改时间
        long lastModified = file.lastModified();
        LocalDateTime modifiedDate = Instant.ofEpochMilli(lastModified)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        return modifiedDate.isBefore(cutoffDate);
    }
} 