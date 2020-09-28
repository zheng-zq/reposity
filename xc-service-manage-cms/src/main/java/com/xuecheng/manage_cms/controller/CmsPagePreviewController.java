package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    @RequestMapping(value = "/cms/preview/{pageId}", method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) throws Exception {
        //执行静态化
        String pageHtml = pageService.getPageHtml(pageId);
        //输出html内容
        ServletOutputStream outputStream = response.getOutputStream();

        //ngix解析ssi  展示拼接渲染后的页面
        response.setHeader("Content-type","text/html;charset=utf-8");

        outputStream.write(pageHtml.getBytes("utf-8"));
    }
}
