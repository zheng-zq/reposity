package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Administrator.
 * mybatis:用于复杂的查询操作  有对应的xml文件:CourseMapper.xml
 */
@Mapper
public interface CourseMapper {
    CourseBase findCourseBaseById(String id);

    Page<CourseBase> findCourseList();

    List<CourseInfo> selectCourseList();
    //我的课程的查询列表
    Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
