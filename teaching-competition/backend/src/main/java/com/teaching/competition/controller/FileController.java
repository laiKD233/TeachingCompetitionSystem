package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final OssService ossService;

    /**
     * 上传文件到 OSS
     * @param file 文件
     * @return 文件完整访问 URL
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        try {
            String url = ossService.upload(file, "files");
            return Result.success(url);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除 OSS 上的文件
     * @param fileUrl 文件完整 URL
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam String fileUrl) {
        ossService.delete(fileUrl);
        return Result.success();
    }
}
