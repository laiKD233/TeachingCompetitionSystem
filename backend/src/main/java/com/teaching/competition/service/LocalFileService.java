package com.teaching.competition.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileService {

    @Value("${file.storage.local-path:uploads}")
    private String configuredStoragePath;

    @Value("${file.storage.domain:}")
    private String fileDomain;

    /**
     * 文件存储根目录（基于项目根目录的绝对路径）
     */
    private String storageRoot;
    private String legacyStorageRoot;

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

    @PostConstruct
    public void init() {
        // 兼容绝对路径与相对路径配置
        File configured = new File(configuredStoragePath);
        if (configured.isAbsolute()) {
            storageRoot = configured.getAbsolutePath();
        } else {
            storageRoot = configured.getAbsoluteFile().getAbsolutePath();
        }

        // 历史版本曾写入 backend/uploads，保留回退读取能力
        String backendPath = new File(".").getAbsolutePath();
        legacyStorageRoot = new File(backendPath, "backend/uploads").getAbsolutePath();
        
        // 确保存储目录存在
        File rootDir = new File(storageRoot);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
            log.info("创建文件存储目录: {}", rootDir.getAbsolutePath());
        }
    }

    /**
     * 上传文件到本地存储，返回可访问的完整 URL
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
        String relativePath = directory + "/" + dateStr + "/" + UUID.randomUUID() + "." + extension.toLowerCase();
        
        try {
            // 创建目标文件路径（使用绝对路径）
            Path targetPath = Paths.get(storageRoot, relativePath);
            Files.createDirectories(targetPath.getParent());
            
            // 保存文件
            file.transferTo(targetPath.toFile());
            log.info("文件上传成功: {}", targetPath.toAbsolutePath());
            
            // 返回访问 URL（优先使用配置域名，否则返回相对路径）
            return buildAccessUrl(relativePath);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除本地存储的文件
     *
     * @param fileUrl 文件完整 URL 或相对路径
     */
    public void delete(String fileUrl) {
        String relativePath = extractRelativePath(fileUrl);
        if (relativePath == null) {
            log.warn("无法解析文件路径，跳过删除: {}", fileUrl);
            return;
        }
        
        try {
            deleteIfExists(resolveStoragePath(storageRoot, relativePath));
            if (legacyStorageRoot != null && !legacyStorageRoot.equals(storageRoot)) {
                deleteIfExists(resolveStoragePath(legacyStorageRoot, relativePath));
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", relativePath, e);
        } catch (IllegalArgumentException e) {
            log.warn("非法文件路径，跳过删除: {}", relativePath);
        }
    }

    /**
     * 根据相对路径获取文件
     *
     * @param relativePath 文件相对路径
     * @return 文件对象
     */
    public File getFile(String relativePath) {
        Path filePath = resolveStoragePath(storageRoot, relativePath);
        if (!Files.exists(filePath) && legacyStorageRoot != null) {
            Path legacyPath = resolveStoragePath(legacyStorageRoot, relativePath);
            if (Files.exists(legacyPath)) {
                return legacyPath.toFile();
            }
        }
        return filePath.toFile();
    }

    /**
     * 从完整 URL 中提取相对路径
     */
    private String extractRelativePath(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return null;
        
        // 如果已经是相对路径（以目录名开头），直接返回
        if (fileUrl.startsWith("works/") || fileUrl.startsWith("covers/") || fileUrl.startsWith("files/")) {
            return fileUrl;
        }

        // 新格式：/api/file/download/xxx
        String downloadPrefix = "/api/file/download/";
        if (fileUrl.startsWith(downloadPrefix)) {
            return fileUrl.substring(downloadPrefix.length());
        }
        
        // 从 URL 中提取路径部分
        try {
            int index = fileUrl.indexOf(downloadPrefix);
            if (index != -1) {
                return fileUrl.substring(index + downloadPrefix.length());
            }
        } catch (Exception e) {
            log.warn("解析文件路径失败: {}", fileUrl, e);
        }
        
        return null;
    }

    private Path resolveStoragePath(String root, String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        Path rootPath = Paths.get(root).toAbsolutePath().normalize();
        Path targetPath = rootPath.resolve(relativePath).normalize();
        if (!targetPath.startsWith(rootPath)) {
            throw new IllegalArgumentException("非法文件路径");
        }
        return targetPath;
    }

    private String buildAccessUrl(String relativePath) {
        String downloadPath = "/api/file/download/" + relativePath;
        if (fileDomain == null || fileDomain.isBlank()) {
            return downloadPath;
        }
        String trimmed = fileDomain.endsWith("/")
                ? fileDomain.substring(0, fileDomain.length() - 1)
                : fileDomain;
        return trimmed + downloadPath;
    }

    private void deleteIfExists(Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("文件删除成功: {}", filePath.toAbsolutePath());
        }
    }
}
