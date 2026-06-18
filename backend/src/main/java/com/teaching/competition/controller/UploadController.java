package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.service.LocalFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final LocalFileService localFileService;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("收到图片上传请求，文件名: {}, 大小: {} bytes", 
                file.getOriginalFilename(), file.getSize());

        if (file == null || file.isEmpty()) {
            log.warn("上传文件为空");
            return Result.error("请选择要上传的文件");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        log.info("文件类型: {}", contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("文件类型不正确: {}", contentType);
            return Result.error("请上传图片文件");
        }

        // 检查文件大小（2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            log.warn("文件大小超过限制: {} bytes", file.getSize());
            return Result.error("图片大小不能超过 2MB");
        }

        try {
            // 使用 LocalFileService 上传图片，存储到 files 目录
            String url = localFileService.upload(file, "files");
            log.info("图片上传成功: {}", url);
            return Result.success(url);
            
        } catch (IllegalArgumentException e) {
            log.warn("上传参数错误: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}