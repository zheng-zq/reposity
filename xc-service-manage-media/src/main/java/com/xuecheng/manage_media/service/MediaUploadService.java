package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

//媒体文件操作
@Service
public class MediaUploadService {

    @Autowired
    MediaFileRepository mediaFileRepository;

    //上传文件根目录
    @Value("${xc-service-manage-media.upload-location}")
    String uploadPath;

    //routingKey
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkey_media_video;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //  得到文件所在目录
    private String getFileFolderPath(String fileMd5) {
        String fileFolderPath = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return fileFolderPath;
    }

    //  得到文件的路径
    private String getFilePath(String fileMd5, String fileExt) {
        String filePath = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }

    //  向MQ发送消息(执行视频处理)
    public ResponseResult sendProcessVideoMsg(String mediaId) {
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            return new ResponseResult(CommonCode.FAIL);
        }
        MediaFile mediaFile = optional.get();
        //1 向MQ发送消息
        HashMap<String, String> msgMap = new HashMap<>();
        msgMap.put("mediaId", mediaId);
        //消息内容
        String jsonString = JSON.toJSONString(msgMap);
        try {
            //发送消息
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK, routingkey_media_video, jsonString);
        } catch (AmqpException e) {
            e.printStackTrace();
            //发送失败
            return new ResponseResult(CommonCode.FAIL);
        }
        //发送成功
        return new ResponseResult(CommonCode.SUCCESS);
    }


    //一 文件上传钱的注册  检查文件是否存在
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1 检查文件在磁盘上是否存在
        //  得到文件所属目录的路径
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        //  文件的路径
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        //  文件是否存在exists
        boolean exists = file.exists();

        //2 检查文件在数据库中是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        //  文件存在直接返回
        if (file.exists() && optional.isPresent()) {
            //  文件存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //  文件不存在则创建目录
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            //上传文件目录创建失败
            fileFolder.mkdirs();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //1 检查分块文件是否存在
    private String getChunkFileFolderPath(String fileMd5) {
        String fileChunkFolderPath = getFileFolderPath(fileMd5) + "/" + "chunks" + "/";
        return fileChunkFolderPath;
    }

    //二 校验文件不存在 校验分块文件是否存在
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {

        //1 检查分块文件是否存在
        //2 得到分块文件的所在目录
        String chunkfileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //块文件的文件名称以1,2,3..序号命名，没有扩展名
        File chunkFile = new File(chunkfileFolderPath + chunk);
        if (chunkFile.exists()) {
            //块文件存在
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, true);
        } else {
            //块文件不存在
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, false);
        }
    }

    //三 校验分块文件不存在则上传分块
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, Integer chunk) {

        //获得分块目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //得到分块文件路径
        File chunkfile = new File(chunkFileFolderPath + chunk);
        File chunkFileFolder = new File(chunkFileFolderPath);
        //检查分块目录  不存在则创建
        if (!chunkFileFolder.exists()) {
            chunkFileFolder.mkdirs();
        }
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();

            outputStream = new FileOutputStream(chunkfile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseResult(CommonCode.SUCCESS);
        }
    }

    //合并文件
    private File mergeFile(File mergeFile, List<File> chunkFileList) {
        try {
            // 如果合并文件已存在则删除
            if (mergeFile.exists()) {
                mergeFile.delete();
            } else {
                //不存在创建新文件
                mergeFile.createNewFile();
            }
            //对块文件进行排序
            Collections.sort(chunkFileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                        return 1;//升序
                    }
                    return -1;//降序
                }
            });
            //创建一个写文件对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            //遍历分块文件开始合并
            //读取文件缓冲区
            byte[] b = new byte[1024];
            for (File chunkFile : chunkFileList) {
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
                int len = -1;
                //读取分块文件
                while ((len = raf_read.read(b)) != -1) {
                    //向合并文件中写数据
                    raf_write.write(b, 0, len);
                }
                raf_read.close();
            }
            raf_write.close();
            return mergeFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //校验文件的md5值
    private boolean checkFileMd5(File mergeFile, String md5) {
        if (mergeFile == null || StringUtils.isEmpty(md5)) {
            return false;
        }
        //进行md5校验
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(mergeFile);
            //得到文件的md5
            String mergeFileMd5 = DigestUtils.md5Hex(inputstream);
            //和传入的md5进行比较
            if (md5.equalsIgnoreCase(mergeFileMd5)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //四 上传完分块则合并分块
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {

        //1 得到分块文件的属目录
        String chunkfileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkfileFolder = new File(chunkfileFolderPath);
        if (!chunkfileFolder.exists()) {
            chunkfileFolder.mkdirs();
        }
        // 分块文件列表
        File[] files = chunkfileFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        //创建一个合并文件
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);

        mergeFile = this.mergeFile(mergeFile, fileList);
        if (mergeFile == null) {
            //合并失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //2 校验文件的md5值是否和前端传入的md5值相同
        boolean checkResult = this.checkFileMd5(mergeFile, fileMd5);
        if (!checkResult) {
            //校验文件失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //3 将文件的信息写入mongodb中
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5 + "." + fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        String filePath1 = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        mediaFile.setFilePath(filePath1);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向MQ发送视频处理消息
        sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
