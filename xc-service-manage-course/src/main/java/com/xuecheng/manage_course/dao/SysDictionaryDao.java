package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysDictionaryDao extends MongoRepository<SysDictionary, String> {

    //根据字典分类id(d_type)查询字典信息(课程等级,学习模式)
    List<SysDictionary> findBydType(String dType);
}
