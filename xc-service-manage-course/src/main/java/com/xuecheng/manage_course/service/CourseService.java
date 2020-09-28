package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired(required = false)
    CmsPageClient cmsPageClient;

    @Autowired(required = false)
    CmsPostPageResult cmsPostPageResult;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    //查询教学计划页面根据课程Id
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

//    //查询我的课程
    public QueryResponseResult findCourseList(Integer page, Integer size) {

        PageHelper.startPage(page, size);
        //查询我的课程列表逐步封装进QueryResponseResult中
        //1  查询我的课程列表
        List<CourseInfo> courseInfoList1 = courseMapper.selectCourseList();
        //2  将查询出来的我的课程列表封装进courseInfoList
        Page<CourseInfo> courseInfoList = (Page<CourseInfo>) courseInfoList1;
        //3  将courseInfoList里的数据封装进queryResult
        QueryResult queryResult = new QueryResult();
        queryResult.setList(Collections.singletonList(courseInfoList.getResult()));
        queryResult.setTotal(courseInfoList.getTotal());
        //4  返回queryResult
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    //查询我的课程
    public QueryResponseResult<CourseInfo> findCourseList(String company_id,
                                                          int page, int size,
                                                          CourseListRequest courseListRequest) {
        if(courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        //将companyId传给dao
        courseListRequest.setCompanyId(company_id);
        if(page<=0){
            page = 0;
        }
        if(size<=0){
            size = 20;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> list = courseListPage.getResult();
        long total = courseListPage.getTotal();
        QueryResult<CourseInfo> courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,courseIncfoQueryResult);
    }

    //添加教学计划页面
    @Transactional//mysql支持事务,增删改添加事务,mongodb数据库不支持事务
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //首先判断父节点名字和课程id是否为空,为空的话响应'不合法的参数'异常
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getPname()) ||
                StringUtils.isEmpty(teachplan.getCourseid())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        String courseid = teachplan.getCourseid();//页面传入的课程id
        String parentid = teachplan.getParentid();//页面传入的父节点id
        //一  判断页面传入的父节点id是否为空
        if (StringUtils.isEmpty(parentid)) {
            //*.1  如果页面传入的父节点id为空则获得该课程的根节点
            parentid = this.getTeachplanRoot(courseid);
        }
        //*.2  如果页面传入的父节点id不为空则正常创建新结点教学计划
        Teachplan teachplanNew = new Teachplan();
        //二  将页面提交的教学计划页面信息拷贝到新结点对象中
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        //*.  设置级别:首先获得父节点的级别,然后将父节点的级别数加1就是它的级别
        //*.1  通过父节点的id查找页面
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        //*.2  通过页面查找父节点的级别
        String grade = parentNode.getParentid();
        if (grade.equals("1")) {
            //添加节点要么是2,要么是3
            teachplanNew.setGrade("2");
        } else {
            teachplanNew.setGrade("3");
        }
        //三  保存新结点教学计划
        teachplanRepository.save(teachplanNew);
        //四  响应操作成功信息
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //*.1  如果页面传入的父节点id为空则查询该课程的根节点(两种情况0&1)
    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        //查询(两种情况1&2)
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            //0  查询不到,自动添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setGrade("1");//设置级别
            teachplan.setParentid("0");//设置父节点
            teachplan.setPname(courseBase.getName());//设置父节点名字
            teachplan.setStatus("0");//设置状态
            teachplan.setCourseid(courseId);//设置课程id
        }
        //1  查询到了,返回根节点id,    通过课程id和父节点id正常情况下只能查询到一个,所以索引为0
        return teachplanList.get(0).getId();
    }

    //我的课程新增按钮:将我的新增的课程页面信息提交到数据库
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        //课程状态status默认为202001(未发布)
        courseBase.setStatus("202001");
        //直接调用courseBaseRepository所继承的JpaRepository中的save方法保存数据到数据库(mysql)
        courseBaseRepository.save(courseBase);
        //返回保存结果
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
    }

    //我的课程查询按钮:根据要修改的课程id查询课程页面信息进行回显
    public CourseBase getCoursebaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    //我的课程更新按钮:根据要修改的课程id更新数据库的课程页面信息
    @Transactional
    public ResponseResult updateCoursebase(String id, CourseBase courseBase) {
        CourseBase one = this.getCoursebaseById(id);
        if (one == null) {
            //抛出异常..
            System.out.println("*one = " + one + "*");
        }
        //修改课程信息
        one.setName(courseBase.getName());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setUsers(courseBase.getUsers());
        one.setDescription(courseBase.getDescription());
        courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据要修改的我的课程id查询课程营销页面信息
    public CourseMarket getCourseMarketById(String courseid) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseid);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    //根据要修改的我的课程id和用户更改的课程营销信息更新课程营销信息
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(id);
        if (one != null) {
            one.setCharge(courseMarket.getCharge());
            one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            one.setPrice(courseMarket.getPrice());
            one.setQq(courseMarket.getQq());
            one.setValid(courseMarket.getValid());
            courseMarketRepository.save(one);
        } else {
            //添加课程营销信息
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            //设置课程id
            one.setId(id);
            courseMarketRepository.save(one);
        }
        return one;
    }

    //添加课程图片
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {
        //1  查询数据库课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (picOptional.isPresent()) {
            //2.1  数据库中有课程图片则更新
            coursePic = picOptional.get();
        }
        //2.2  数据库中没有课程图片则新建对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        //3  保存课程图片
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //根据课程id查询课程图片然后回显
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            //2.1  数据库中有课程图片则更新
            return picOptional.get();
        }
        return null;
    }

    //根据课程id删除课程回显图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        //执行删除图片,删除成功返回1,不成功返回0
        long num = coursePicRepository.deleteByCourseid(courseId);
        if (num > 0) {
            //删除成功
            return new ResponseResult(CommonCode.SUCCESS);
        }
        //删除失败
        return new ResponseResult(CommonCode.FAIL);
    }

    //根据课程id查询课程视图
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //查课程基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (optional.isPresent()) {
            courseView.setCourseBase(optional.get());
        }

        //查课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            courseView.setCourseMarket(courseMarketOptional.get());
        }

        //查课程图片信息
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            courseView.setCoursePic(picOptional.get());
        }

        //查课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    //根据课程id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (courseBaseOptional.isPresent()) {
            return courseBaseOptional.get();
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);//课程信息为空
        return null;
    }

    //3 页面预览
    public CoursePublishResult preview(String id) {
        //请求cms添加页面
        //1  远程调用cms模块中的saveCmsPage方法获得课程页面,进而获得页面id
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre + id);//DataUrl
        cmsPage.setPageName(id + ".html");//页面名字
        cmsPage.setTemplateId(publish_templateId);//模版id
        cmsPage.setPageWebPath(publish_page_webpath);//页面路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        CourseBase courseBase = this.findCourseBaseById(id);//在方法外定义一个方法查询课程名称  课程别名就是课程名字
        cmsPage.setPageAliase(courseBase.getName());//课程别名就是课程名字
        //**********
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);

        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);//操作失败
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //2  由页面id生成预览url
        String pageUrl = previewUrl + pageId;
        //3  返回课程页面预览url
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    //一键发布之课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //(1) 调用cms一键发布接口将课程详情页面发布到服务器
        //请求cms添加页面
        //1  远程调用cms模块中的saveCmsPage方法获得课程页面,进而获得页面id
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre + id);//DataUrl
        cmsPage.setPageName(id + ".html");//页面名字
        cmsPage.setTemplateId(publish_templateId);//模版id
        cmsPage.setPageWebPath(publish_page_webpath);//页面路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        CourseBase courseBase = this.findCourseBaseById(id);//在方法外定义一个方法查询课程名称  课程别名就是课程名字
        cmsPage.setPageAliase(courseBase.getName());//课程别名就是课程名字
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);//操作失败
        }
        System.out.println(cmsPageResult.getCmsPage().getDataUrl());
        //(2) 保存课程的发布状态为"已发布"  202002
        CourseBase courseBase1 = this.saveCoursePubState(id);
        if (courseBase1 == null) {
            return new CoursePublishResult(CommonCode.FAIL, null);//操作失败
        }
        //(3) 保存课程索引信息
        //(4) 创建coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //(5) 将coursePub保存到数据库
        saveCoursePub(id, coursePub);

        //(6) 缓存课程的信息
        //...
        //(7) 得到页面的url
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teachplanmediapub中保存课程媒体信息

        //  向teachplanmediapub中保存课程媒体信息
        saveTeachplanMediaPub(id);

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }


    //  向teachplanmediapub中保存课程媒体信息
    private void saveTeachplanMediaPub(String courseId) {
        //  查询课程媒资信息
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        //  将课程计划媒资信息存储待索引表
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachplanMediaPubList);
    }

    //(5) 将coursePub保存到数据库
    private CoursePub saveCoursePub(String id, CoursePub coursePub) {
        CoursePub coursePubNew = null;
        //根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        } else {
            coursePubNew = new CoursePub();
        }
        //将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub, coursePubNew);
        coursePubNew.setId(id);
        coursePubNew.setTimestamp(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");//发布时间  首先设置发布时间格式
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }


    //(4) 创建coursePub对象
    private CoursePub createCoursePub(String id) {
        CoursePub coursePub = new CoursePub();
        coursePub.setId(id);
        //复制数据库中各表的属性到coursePub中
        //课程基础信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional != null) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //课程图片信息
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()) {
            CoursePic coursePic = coursePicOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String teachPlanString = JSON.toJSONString(teachplanNode);//课程计划特殊   转为json字符串存到coursePub中
        coursePub.setTeachplan(teachPlanString);
        return coursePub;
    }

    //(2) 保存课程的发布状态为"已发布"
    private CourseBase saveCoursePubState(String courseId) {
        //根据课程id查询课程页面
        CourseBase courseBase = this.findCourseBaseById(courseId);
        //课程页面设置状态为202002
        courseBase.setStatus("202002");
        //调用mongoDb中的方法保存课程页面
        courseBaseRepository.save(courseBase);
        //返回改变课程状态的课程页面
        return courseBase;
    }

    //保存课程计划与媒体文件关联信息
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
//        if (teachplanMedia == null) {
        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否为三级
        //  获得教学计划id
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(teachplanId);
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //  获得教学计划
        Teachplan teachplan = teachplanOptional.get();
        //  获得等级
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            //  只允许选择第三级的课程计划关联视频!
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //  根据教学计划teachplanId查询教学媒体信息
        Optional<TeachplanMedia> teachplanMediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if (teachplanMediaOptional.isPresent()) {
            one = teachplanMediaOptional.get();
        } else {
            one = new TeachplanMedia();
        }

        //  将teachplanmedia保存到数据库
        //  根据得到的数据设置one中的属性值
        one.setCourseId(teachplan.getCourseid());//课程id
        one.setMediaId(teachplanMedia.getMediaId());//媒体文件的id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//媒体文件的原始名称
        one.setMediaUrl(teachplanMedia.getMediaUrl());//媒体文件的url
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
