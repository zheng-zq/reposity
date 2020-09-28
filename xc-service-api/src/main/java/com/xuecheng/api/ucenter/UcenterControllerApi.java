package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;

//用户中心对外提供如下接口：
@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi {
    public XcUserExt getUserext(String username);
}