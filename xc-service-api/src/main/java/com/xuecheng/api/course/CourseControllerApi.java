package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

@Api(value = "课程管理接口", description = "课程管理接口,提供课程页面的增、删、改、查")
//@Api 表示标识这个类是swagger的资源
public interface CourseControllerApi {

    //查询教学计划页面根据课程Id
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);

    //添加教学计划页面
    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);

    /*@ApiOperation("我的课程列表查询")
    public List<CourseInfo> findCourseList();*/

    //查询我的课程列表并进行分页(通过指定页面page,每页显示条数size)
    /*@ApiOperation("查询我的课程列表")
    public QueryResponseResult findCourseList(@PathVariable("/page") Integer page, @PathVariable("/size") Integer size);*/

    //我的课程新增按钮:将我的新增的课程页面信息提交到数据库
    @ApiOperation("添加课程基础信息")
    public AddCourseResult addCourseBase(CourseBase courseBase);

    //根据要修改的我的课程id查询课程页面信息
    @ApiOperation("获取课程基础信息")
    public CourseBase getCourseBaseById(String courseId) throws RuntimeException;

    //根据要修改的我的课程id更新课程页面信息
    @ApiOperation("更新课程基础信息")
    public ResponseResult updateCourseBase(String id, CourseBase courseBase);

    //根据要修改的我的课程id获取课程营销信息
    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);

    //根据要修改的我的课程id和用户更改的课程营销信息更新课程营销信息
    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

    //添加课程图片
    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId, String pic);

    //根据课程id查询课程图片然后回显
    @ApiOperation("根据课程id查询课程图片然后回显")
    public CoursePic findCoursePic(String courseId);

    //根据课程id删除课程回显图片
    @ApiOperation("根据课程id删除课程回显图片")
    ResponseResult deleteCoursePic(String courseId);

    //根据课程id查询课程视图
    @ApiOperation("课程视图查询")
    CourseView courseview(String id);

    //2 定义接口
    @ApiOperation("课程预览")
    CoursePublishResult preview(String id);

    //一键发布之页面发布
    @ApiOperation("发布课程")
    CoursePublishResult publish(String id);

    //保存课程计划与媒体文件关联
    @ApiOperation("保存课程计划与媒体文件关联")
    ResponseResult savemedia(TeachplanMedia teachplanMedia);

    //查询我的课程列表并进行分页(通过指定页面page,每页显示条数size)
    @ApiOperation("查询我的课程列表")
    public QueryResponseResult<CourseInfo> findCourseList(int page,
                                                          int size,
                                                          CourseListRequest courseListRequest);

}