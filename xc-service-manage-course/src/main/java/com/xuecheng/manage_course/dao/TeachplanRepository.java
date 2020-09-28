package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachplanRepository extends JpaRepository<Teachplan, String> {
    //根据课程Id和parentid查询教学计划
    //  SELECT * FROM `teachplan` a WHERE a.`courseid` = '4028e581617f945f01617f9dabc40000' AND a.`parentid` = '0'
    public List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
