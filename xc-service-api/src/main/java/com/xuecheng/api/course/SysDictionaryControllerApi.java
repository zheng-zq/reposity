package com.xuecheng.api.course;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value = "数据字典接口", description = "提供数据字典接口的管理、查询功能")
public interface SysDictionaryControllerApi {
    //数据字典
    @ApiOperation(value = "数据字典查询接口")
    public List<SysDictionary> getByType(String type);
}