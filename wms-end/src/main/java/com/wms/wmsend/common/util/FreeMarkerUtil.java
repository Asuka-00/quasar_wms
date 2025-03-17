package com.wms.wmsend.common.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreeMarkerUtil {

    private static final String TEMPLATE_DIRECTORY = "/templates"; // 模板目录

    private static Configuration cfg;

    static {
        try {
            // 初始化 FreeMarker 配置
            cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setClassForTemplateLoading(FreeMarkerUtil.class, TEMPLATE_DIRECTORY);
            cfg.setDefaultEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 HTML 内容
     *
     * @param templateName 模板文件名称
     * @param dataModel 数据模型
     * @return 生成的 HTML 内容
     * @throws IOException
     * @throws TemplateException
     */
    public static String generateHtml(String templateName, Map<String, Object> dataModel) throws IOException, TemplateException {
        // 加载模板
        Template template = cfg.getTemplate(templateName);

        // 将模板和数据模型合并，并生成 HTML
        StringWriter stringWriter = new StringWriter();
        template.process(dataModel, stringWriter);

        return stringWriter.toString();
    }


    public static Map<String, Object> nullToEmpty(Map<String, Object> dataSource) {
        dataSource.forEach((k, v) -> {
            if (v == null) {
                dataSource.put(k, "");
            }
        });
        return dataSource;
    }
}

