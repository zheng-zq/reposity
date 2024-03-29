package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//接收视频处理消息进行视频处理
@Component
public class MediaProcessTask {
    @Autowired
    MediaFileRepository mediaFileRepository;

    //ffmpeg绝对路径
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;

    //上传视频源地址
    @Value("${xc-service-manage-media.video-location}")
    String serverPath;

    //监听队列
    @RabbitListener(queues = {"${xc-service-manage-media.mq.queue-media-video-processor}"},
            containerFactory="customContainerFactory")
    public void receiveMediaProcessTask(String msg) {
        //1 解析消息内容获得mediaId
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //2 根据mediaId从mongoDb中查询avi的文件视频信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            return;
        }
        MediaFile mediaFile = optional.get();
        //  只处理avi格式的,所以在这里先判断是不是avi格式
        String fileType = mediaFile.getFileType();
        if (!"avi".equals(fileType)) {
            mediaFile.setProcessStatus("303004");//"无需处理"
            mediaFileRepository.save(mediaFile);
            return;
        }
        mediaFile.setProcessStatus("303001");//avi格式需要处理显示"处理中"
        //3 使用工具类将视频文件转成mp4
        //  要处理的视频文件的路径D:/WebStorm/xcEDU/xczx_ws02/xc-static-pages/video/  c/5/c5c75d70f382e6016d2f506d134eee11/  c5c75d70f382e6016d2f506d134eee11.avi
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();//上传视频源地址+文件路径+文件名字
        //  生成的mp4的文件名称c5c75d70f382e6016d2f506d134eee11 .mp4
        String mp4_name = mediaFile.getFileId() + ".mp4";
        //  生成的mp4所在的路径D:/WebStorm/xcEDU/xczx_ws02/xc-static-pages/video/  c/5/c5c75d70f382e6016d2f506d134eee11/
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        //  创建工具类对象
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4folder_path);
        String result = mp4VideoUtil.generateMp4();
        if (result == null || !"success".equals(result)) {
            //处理失败
            mediaFile.setProcessStatus("303003");
            //定义mediaFileProcess_m3u8
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            //记录失败原因
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //4 将mp4文件转成m3u8和ts文件
        //  mp4视频文件路径D:/WebStorm/xcEDU/xczx_ws02/xc-static-pages/video/  c/5/c5c75d70f382e6016d2f506d134eee11/  c5c75d70f382e6016d2f506d134eee11.mp4
        String mp4_video_path = serverPath + mediaFile.getFilePath() + mp4_name;
        //  m3u8_name文件名称c5c75d70f382e6016d2f506d134eee11 .m3u8
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        //  .m3u8文件所在目录D:/WebStorm/xcEDU/xczx_ws02/xc-static-pages/video/  hls/
        String m3u8folder_path = serverPath + mediaFile.getFilePath() + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, mp4_video_path, m3u8_name, m3u8folder_path);
        //  生成m3u8和ts文件操作的结果
        String tsResult = hlsVideoUtil.generateM3u8();
        if (tsResult == null || !"success".equals(tsResult)) {
            //处理失败
            mediaFile.setProcessStatus("303003");
            //定义mediaFileProcess_m3u8
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            //记录失败原因
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //  处理成功
        //  获取ts列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        mediaFile.setProcessStatus("303002");
        //  定义mediaFileProcess_m3u8
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        //  记录成功原因
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);

        //  保存fileUrl(此url就是视频播放的相对路径)
        //  视频播放的相对路径8/0/809694a6a974c35e3a36f36850837d7c/ hls/ c5c75d70f382e6016d2f506d134eee11.m3u8
        String fileUrl = mediaFile.getFilePath() + "hls/" + m3u8_name;
        mediaFile.setFileUrl(fileUrl);
        mediaFileRepository.save(mediaFile);
    }
}
