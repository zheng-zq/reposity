package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")//category类别
public class CategoryController implements CategoryControllerApi {

    @Autowired
    CategoryService categoryService;

    //查询课程分类树形图
    @GetMapping("/list")
    public CategoryNode findList() {
        return categoryService.findList();
    }
}