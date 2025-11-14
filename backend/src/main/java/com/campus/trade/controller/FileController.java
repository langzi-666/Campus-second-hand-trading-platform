package com.campus.trade.controller;

import com.campus.trade.common.Result;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传控制器
 * 
 * @author 开发团队
 */
@RestController
@RequestMapping("/files")
// @Api(tags = "文件管理")
@CrossOrigin
public class FileController {
    
    @Value("${file.upload.path:/uploads/}")
    private String uploadPath;
    
    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,bmp}")
    private String allowedTypes;
    
    @Value("${file.upload.max-size:5242880}")
    private Long maxSize;
    
    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    // @ApiOperation("单个文件上传")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = saveFile(file);
            return Result.success("文件上传成功", fileUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传多个文件
     */
    @PostMapping("/upload/batch")
    // @ApiOperation("多个文件上传")
    public Result<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileUrl = saveFile(file);
                fileUrls.add(fileUrl);
            }
            return Result.success("文件上传成功", fileUrls);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }
        
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedFileType(fileExtension)) {
            throw new RuntimeException("不支持的文件类型，只支持：" + allowedTypes);
        }
        
        // 生成新的文件名
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        
        // 创建日期目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullUploadPath = uploadPath + dateDir;
        
        // 创建目录
        Path uploadDir = Paths.get(fullUploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 保存文件
        Path filePath = uploadDir.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath);
        
        // 返回文件访问URL
        return "/" + dateDir + "/" + newFileName;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedFileType(String fileExtension) {
        String[] allowedTypesArray = allowedTypes.split(",");
        for (String allowedType : allowedTypesArray) {
            if (allowedType.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
}
