package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    XcMenuMapper xcMenuMapper;


    //根据账户查询用户信息,返回用户扩展信息
    public XcUserExt getUserExt(String username) {
        //1 根据用户账号查询用户信息
        XcUser xcUser = this.findXcUserByUsername(username);
        if (xcUser == null) {
            return null;
        }
        //用户id
        String userId = xcUser.getId();
        //2 根据用户id查询用户公司信息  所有权限*****
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        //****************************************
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        String companyId = null;
        if (xcCompanyUser != null) {
            //3 获得公司id
            companyId = xcCompanyUser.getCompanyId();
        }
        XcUserExt xcUserExt = new XcUserExt();
        //复制属性
        BeanUtils.copyProperties(xcUser, xcUserExt);
        xcUserExt.setCompanyId(companyId);
        //设置权限*********
        xcUserExt.setPermissions(xcMenus);
        //****************************************
        return xcUserExt;
    }

    //根据用户账号查询用户信息
    public XcUser findXcUserByUsername(String username) {
        return xcUserRepository.findXcUserByUsername(username);
    }

}
