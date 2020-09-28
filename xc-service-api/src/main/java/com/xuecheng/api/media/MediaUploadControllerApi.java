package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;
//媒体文件操作
@Api(value = "媒资管理接口", description = "媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {

    //文件上传前的准备工作  校验文件是否存在
    @ApiOperation("文件上传注册")
    public ResponseResult register(String fileMd5,//文件md5码
                                   String fileName,//文件名称
                                   Long fileSize,
                                   String mimetype,//文件类型
                                   String fileExt);//文件扩展名

    //校验文件不存在 校验分块文件是否存在
    @ApiOperation("分块检查")
    public CheckChunkResult checkchunk(String fileMd5,
                                       Integer chunk,//块的下标
                                       Integer chunkSize);//快的大小

    //校验分块文件不存在则上传分块
    @ApiOperation("上传分块")
    public ResponseResult uploadchunk(MultipartFile file,//文件上传域的name
                                      Integer chunk,
                                      String fileMd5);

    //上传完分块则合并分块
    @ApiOperation("合并文件")
    public ResponseResult mergechunks(String fileMd5,
                                      String fileName,
                                      Long fileSize,
                                      String mimetype,
                                      String fileExt);
}
