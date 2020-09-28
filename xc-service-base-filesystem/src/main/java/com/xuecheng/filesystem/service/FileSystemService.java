package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileSystemService {

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param filetag       文件标签
     * @param businesskey   业务key
     * @param metedata      元信息,json格式
     * @return
     */
    //上传文件
    @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile,//文件
                                   String filetag,//文件标签
                                   String businesskey,//业务key
                                   String metadata//元信息,json格式
    ) {
        //第一步:将文件上传到fastDfS中,得到一个文件id
        String fileId = fdfs_upload(multipartFile);
        if (StringUtils.isEmpty(fileId)) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);//fileId为空,上传文件服务器失败
        }
        //第二步:将文件id及其他文件信息存储到mongodb中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFilePath(fileId);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFiletag(filetag);
        fileSystem.setFileType(multipartFile.getContentType());
        if (StringUtils.isEmpty(metadata)) {
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);

    }

    //第一步:将文件上传到fastDfS中,得到一个文件id
    private String fdfs_upload(MultipartFile multipartFile) {
        //1  初始化fastDFS的环境
        initFdfsConfig();
        //2  创建trackerClient,用以保存上传文件引导信息fileId
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            //3  得到storage服务器
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //4  创建storageClient来上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);
            //5  上传文件
            byte[] bytes = multipartFile.getBytes();//得到文件字节
            //6  得到文件的原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //7  得到文件的扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_appender_file1(bytes, ext, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //1  初始化fast环境
    private void initFdfsConfig() {
        //  初始化tracker服务地址(多个tracker中间以半角逗号分隔)
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            //  抛出异常
            ExceptionCast.cast(FileSystemCode.FS_INITDFSERROR);
        }

    }
}
