package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
//定义可以在本地调用的feign接口   注意:接口的Url、请求参数类型(get)、返回值类型(CmsPage)与Swagger接口一致
@FeignClient(value = "XC-SERVICE-MANAGE-CMS")//指定远程调用的服务名
public interface CmsPageClient {
    //根据页面courseid查询页面信息  远程调用cms请求数据
    @GetMapping("/cms/page/get{id}")//用GetMapping标识远程调用的http的方法类型
    public CmsPage findCmsPageById(@PathVariable("id") String id);
}*/

//@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)
@FeignClient("xc-service-manage-cms")
public interface CmsPageClient {

    //根据课程id查找课程页面
    @GetMapping("/cms/page/get/{id}")
    public CmsPage findById(@PathVariable("id") String id);

    //添加页面,用于课程预览
    @PostMapping("/cms/page/save")
    CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);

    //一键发布之页面发布
    @PostMapping("cms/page/postPageQuick")
    CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);
}
