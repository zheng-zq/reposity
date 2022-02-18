package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
//如果需要返回JSON，XML或自定义mediaType内容到页面，则需要在对应的方法上加上@ResponseBody注解
//@Controller
@RequestMapping("/course")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//激活方法上的PreAuthorize注解  启用全局方法安全
public class CourseController extends BaseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;

    //查询教学计划页面根据课程Id
    @Override
    @PreAuthorize("hasAuthority('course_teachplan_list')")//拥有course_teachplan_list权限的用户才可以访问此方法。
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {//@PathVariable路径值 值来自于前端传过来的url路径
        return courseService.findTeachplanList(courseId);
    }

    //添加教学计划页面
    @Override
    @PreAuthorize("hasAuthority('course_teachplan_add')")//拥有course_teachplan_list权限的用户才可以访问此方法。
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {//@RequestBody 将前端传过来的数据转换为json数据
        return courseService.addTeachplan(teachplan);
    }

    //查询我的课程
    @GetMapping("/course/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") Integer page,
                                              @PathVariable("size") Integer size
    ) {
        QueryResponseResult courseList = courseService.findCourseList(page, size);
        return courseList;
    }


    //我的课程新增按钮:将我的新增的课程页面信息提交到数据库
    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    //我的课程查询按钮:根据要修改的课程id查询课程页面信息进行回显
    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable String courseId) throws RuntimeException {
        return courseService.getCoursebaseById(courseId);
    }

    //我的课程更新按钮:根据要修改的课程id更新数据库的课程页面信息
    @Override
    @PutMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable String id, @RequestBody CourseBase courseBase) {
        return courseService.updateCoursebase(id, courseBase);
    }


    //根据要修改的我的课程id查询课程营销页面信息
    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    //根据要修改的我的课程id和用户更改的课程营销信息更新课程营销信息
    @Override
    @PostMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        CourseMarket courseMarket_u = courseService.updateCourseMarket(id, courseMarket);
        if (courseMarket_u != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //添加课程图片
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.saveCoursePic(courseId, pic);
    }

    //根据课程id查询课程图片然后回显
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }

    //根据课程id删除课程回显图片
    @Override
    @PreAuthorize("hasAuthority('course_find_pic')")
    //拥有course_teachplan_list权限的用户才可以访问此方法。
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    //根据课程id查询课程视图
    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return courseService.getCoruseView(id);
    }

    //3 页面预览
    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    //一键发布之页面发布
    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    //保存课程计划与媒体文件关联
    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }

    //查询我的课程列表并进行分页(通过指定页面page,每页显示条数size)
//    @GetMapping("/coursebase/list/{page}/{size}")
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page,
                                              @PathVariable("size") int size,
                                              CourseListRequest courseListRequest) {
        //调用工具类取出用户信息
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt == null) {
            ExceptionCast.cast(CommonCode.UNAUTHENTICATED);
        }
//        //先使用静态数据测试
//        String company_id = "2";
        return courseService.findCourseList(userJwt.getCompanyId(), page, size, courseListRequest);
    }


}
