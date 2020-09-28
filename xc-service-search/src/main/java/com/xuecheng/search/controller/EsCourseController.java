package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//测试课程搜索方法  eSearch
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;

    //通过搜索查询的响应结果
    @GetMapping(value = "/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page, @PathVariable("size") int size, CourseSearchParam courseSearchParam) throws Exception {
        return esCourseService.list(page, size, courseSearchParam);
    }

    //根据课程id查询课程信息
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getall(@PathVariable("id") String id) {
        return esCourseService.getall(id);
    }

    //根据课程计划id查询课程媒资信息
    @GetMapping(value = "/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId) {
        //1 将课程计划id放在数组中,为调用service做准备
        String[] teachplanIds = {teachplanId};

        //2 通过service查询ES获取课程媒体信息
        QueryResponseResult<TeachplanMediaPub> mediaPubQueryResponseResult = esCourseService.getmedia(teachplanIds);
        QueryResult<TeachplanMediaPub> queryResult = mediaPubQueryResponseResult.getQueryResult();
        if (queryResult != null && queryResult.getList() != null && queryResult.getList().size() > 0) {
            //3 返回课程计划对应课程媒体信息
            return queryResult.getList().get(0);
        }
        return new TeachplanMediaPub();
    }
}
