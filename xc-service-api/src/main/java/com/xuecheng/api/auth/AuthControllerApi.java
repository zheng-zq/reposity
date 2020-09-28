package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//认证服务Api接口
@Api(value = "用户认证", description = "用户认证接口")
public interface AuthControllerApi {

    //登陆需要令牌 用户名 密码
    @ApiOperation("登录")
    LoginResult login(LoginRequest loginRequest);

    //退出清除令牌清除cookie
    @ApiOperation("退出")
    ResponseResult logout();

    @ApiOperation("查询用户jwt令牌")
    JwtResult userjwt();
}
