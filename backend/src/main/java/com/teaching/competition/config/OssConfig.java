package com.teaching.competition.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class OssConfig {

    /**
     * 文件存储类型：local 或 oss
     */
    private String type = "local";

    /**
     * 本地存储根目录
     */
    private String localPath = "backend/uploads";

    /**
     * 访问域名，用于构建文件访问 URL
     */
    private String domain;

    /**
     * 阿里云 OSS 相关配置（保留以便将来可能的扩展）
     */
    private Oss oss = new Oss();

    @Data
    public static class Oss {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String proxy;
    }

    /**
     * 获取文件访问的完整 URL（本地存储时返回相对路径）
     */
    public String getFileUrl(String objectName) {
        if (domain != null && !domain.isBlank()) {
            return domain.endsWith("/") ? domain + objectName : domain + "/" + objectName;
        }
        return "/api/file/download/" + objectName;
    }
}