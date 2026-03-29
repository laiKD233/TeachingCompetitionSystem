package com.teaching.competition.service;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.teaching.competition.config.OssConfig;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    /**
     * 允许上传的文件扩展名
     */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            // 文档
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "csv",
            // 图片
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg",
            // 视频
            "mp4", "avi", "mov", "wmv", "flv", "mkv",
            // 音频
            "mp3", "wav", "flac", "aac",
            // 代码
            "java", "py", "js", "ts", "html", "css", "json", "xml", "sql", "c", "cpp", "h",
            // 压缩包
            "zip", "rar", "7z", "tar", "gz"
    );

    /**
     * 单个文件最大 200MB
     */
    private static final long MAX_FILE_SIZE = 200 * 1024 * 1024L;

    /**
     * 上传文件到 OSS，返回可访问的完整 URL
     *
     * @param file      上传的文件
     * @param directory 存储子目录，如 "works"、"covers"
     * @return 文件完整访问 URL
     */
    public String upload(MultipartFile file, String directory) {
        // 校验文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 200MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = StrUtil.subAfter(originalFilename, ".", true);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }

        // 按日期 + 目录组织：works/20260329/uuid.ext
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String objectName = directory + "/" + dateStr + "/" + UUID.randomUUID() + "." + extension.toLowerCase();

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream, metadata);
            String url = ossConfig.getFileUrl(objectName);
            log.info("文件上传成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除 OSS 上的文件
     *
     * @param fileUrl 文件完整 URL 或 objectName
     */
    public void delete(String fileUrl) {
        String objectName = extractObjectName(fileUrl);
        if (objectName == null) {
            log.warn("无法解析文件路径，跳过删除: {}", fileUrl);
            return;
        }
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", objectName, e);
        }
    }

    /**
     * 从完整 URL 中提取 objectName
     * 例如：https://bucket.oss-cn-hangzhou.aliyuncs.com/works/20260329/uuid.pdf
     *       -> works/20260329/uuid.pdf
     */
    private String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return null;
        // 如果已经是 objectName（以目录名开头），直接返回
        if (fileUrl.startsWith("works/") || fileUrl.startsWith("covers/")) {
            return fileUrl;
        }
        try {
            String domain = ossConfig.getDomain();
            if (domain != null && !domain.isBlank()) {
                String prefix = domain.endsWith("/") ? domain : domain + "/";
                if (fileUrl.startsWith(prefix)) {
                    return fileUrl.substring(prefix.length());
                }
            }
            // 默认格式：https://bucket.endpoint/objectName
            String prefix = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/";
            if (fileUrl.startsWith(prefix)) {
                return fileUrl.substring(prefix.length());
            }
        } catch (Exception e) {
            log.warn("解析 objectName 失败: {}", fileUrl, e);
        }
        return null;
    }

    @PreDestroy
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
