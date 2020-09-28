package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/filesystem")
@RestController
public class FileSystemController implements FileSystemControllerApi {
    @Autowired
    FileSystemService fileSystemService;


    //将文件信息存入数据库，主要存储文件系统中的文件路径。
    @PostMapping("/upload")
    public UploadFileResult upload(/*@RequestParam("multipartFile")*/ MultipartFile multipartFile,//文件
                                   /*@RequestParam("filetag")*/ String filetag,//文件标签
                                   /*@RequestParam(value = "businesskey", required = false)*/ String businesskey,//业务key
                                   /*@RequestParam(value = "metadata", required = false)*/ String metadata//元信息,json格式
    ) {
        return fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
    }
}
