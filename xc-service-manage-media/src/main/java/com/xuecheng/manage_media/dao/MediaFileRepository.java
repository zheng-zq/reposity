package com.xuecheng.manage_media.dao;

import com.xuecheng.framework.domain.media.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

//媒体文件操作
public interface MediaFileRepository extends MongoRepository<MediaFile,String> {
}
