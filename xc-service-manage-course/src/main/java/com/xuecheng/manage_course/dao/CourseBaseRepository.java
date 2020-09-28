package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 * Spring data Jpa:用于表的基本CRUD
 */
public interface CourseBaseRepository extends JpaRepository<CourseBase, String> {
}
