package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.dao.SysDictionaryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysdictionaryService {
    @Autowired
    SysDictionaryDao sysDictionaryDao;

    //根据字典分类id(d_type)查询字典信息(课程等级,学习模式)
    public List<SysDictionary> findDictionaryByType(String type) {
        return sysDictionaryDao.findBydType(type);
    }
}