package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.service.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final LocalFileService localFileService;

    /**
     * 上传文件到本地存储
     * @param file 文件
     * @return 文件完整访问 URL
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        try {
            String url = localFileService.upload(file, "files");
            return Result.success(url);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param filePath 文件相对路径
     * @return 文件资源
     */
    @GetMapping("/download/{*filePath}")
    public ResponseEntity<Resource> download(@PathVariable String filePath) {
        try {
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            File file = localFileService.getFile(filePath);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);
            String fileName = file.getName();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null || contentType.isBlank()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            boolean inlinePreview = contentType.startsWith("image/");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            (inlinePreview ? "inline" : "attachment")
                                    + "; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除本地存储的文件
     * @param fileUrl 文件完整 URL
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam String fileUrl) {
        localFileService.delete(fileUrl);
        return Result.success();
    }
}
