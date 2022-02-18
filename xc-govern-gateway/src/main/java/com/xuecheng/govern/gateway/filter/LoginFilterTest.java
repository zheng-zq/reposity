package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//定义过虑器，使用@Component标识为bean。
@Component
public class LoginFilterTest extends ZuulFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginFilterTest.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;//int值来定义过滤器的执行顺序，数值越小优先级越高
    }

    @Override
    public boolean shouldFilter() {// 该过滤器需要执行
        return true;
    }

    //过滤器的逻辑运行
    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        //得到response
        HttpServletResponse response = requestContext.getResponse();
        //取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            // 拒绝访问
            requestContext.setSendZuulResponse(false);
            // 设置响应状态码
            requestContext.setResponseStatusCode(200);
            //构成响应的信息
            ResponseResult unauthenticated = new ResponseResult(CommonCode.UNAUTHENTICATED);
            //转成json
            String jsonString = JSON.toJSONString(unauthenticated);
            requestContext.setResponseBody(jsonString);
            //转成json,设置contentType
            response.setContentType("application/json;charset=UTF-8");
            return null;
        }
        return null;
    }

}
