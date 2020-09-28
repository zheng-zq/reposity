package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Test
    public void testGenerateHtmlByString()throws Exception{
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //定义模版
        //获取模版路径
        //获取模版文件的内容
        String templateString="" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                " 名称：${name}\n" +
                " </body>\n" +
                "</html>";
        //使用模版加载器变为模版
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateString);
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模版的内容
        Template template = configuration.getTemplate("template", "utf-8");
        //定义数据类型
        Map map = getMap();
        //静态化
        String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        System.out.println(s);
        InputStream inputStream = IOUtils.toInputStream(s);
        //输出文件到指定路径
        FileOutputStream outputStream = new FileOutputStream(new File("D:/test1.html"));
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    //测试静态化  基于ftl模版文件生成html文件
    @Test
    public void testGenerateHtml()throws Exception{
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //定义模版
        //获取模版路径
        String path = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(path+"/templates"));
        //获取模版文件的内容
        Template template = configuration.getTemplate("test1.ftl");
        //定义数据类型
        Map map = getMap();
        //静态化
        String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        System.out.println(s);
        InputStream inputStream = IOUtils.toInputStream(s);
        //输出文件到指定路径
        FileOutputStream outputStream = new FileOutputStream(new File("D:/test1.html"));
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    public Map getMap(){
        HashMap<Object, Object> map = new HashMap<>();

        //map就是freemarker模版所使用的数据
        //向数据模型放数据
        map.put("name", "黑马程序员");

        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        //朋友列表
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        //给第二名学生设置学生列表
        stu2.setFriends(friends);
        //给第二名学生设置最好的朋友
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //将学生列表放在map数据模型中
        map.put("stus", stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放数据
        map.put("stu1", stu1);
        //向数据模型放map数据
        map.put("stuMap", stuMap);

        return map;
    }
}
