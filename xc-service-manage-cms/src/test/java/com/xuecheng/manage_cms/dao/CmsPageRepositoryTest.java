package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll() {
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    //分页查询
    public void testFindPage() {
        //分页参数
       int page = 2;//从0开始
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    @Test
    //自定义条件查询
    public void testFindPageByExample() {
        //分页参数
        int page = 0;//从0开始
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);

        CmsPage cmsPage = new CmsPage();//条件值对象
//        cmsPage.setPageName("测试页面");
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setPageAliase("轮播");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();//条件匹配器
         exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);//定义example
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }

    @Test
    //添加查询
    public void testInsert() {
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("A01");
        cmsPage.setTemplateId("A01");
        cmsPage.setPageName("郑治青");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> list = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("郑治青");
        cmsPageParam.setPageParamValue("郑治青");
        list.add(cmsPageParam);
        cmsPage.setPageParams(list);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    @Test
    //删除
    public void testDelete() {
       cmsPageRepository.deleteById("5dd8cfb259d4346ac84ba137");
        System.out.println(11111111);
    }



    //修改
    @Test
    public void testUpdate(){
        //查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("aaa942190e661827d8e2f5e3");
        if(optional.isPresent()){
            //查询
            CmsPage cmsPage = optional.get();
            //修改
            cmsPage.setPageAliase("郑治青");
            //保存
            CmsPage save = cmsPageRepository.save(cmsPage);
            //输出
            System.out.println(save);
            System.out.println("11111111111");
        }
    }

    //根据页面名称查询
    @Test
    public void testfindByPageName(){
        CmsPage cs = cmsPageRepository.findByPageName("测试页面");
        System.out.println(cs);
    }
}
