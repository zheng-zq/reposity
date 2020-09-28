package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

//根据要修改的我的课程id和用户更改的课程营销信息更新课程营销信息
public interface CourseMarketRepository extends JpaRepository<CourseMarket, String> {
}