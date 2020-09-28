package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUser {
    @Autowired
    private UserService userService;

    @Test
    public void test21(){
        XcUserExt itcast = userService.getUserExt("itcast");
        System.out.println(itcast);
    }
}
