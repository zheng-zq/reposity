package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.ToString;

/**
 * @Author: mrt.
 * @Description:
 * @Date:Created in 2018/1/24 18:33.
 * @Modified By:
 */

@ToString
public enum LearningCode implements ResultCode {
    /**
     *
     */
    LEARNING_GETMEDIA_ERROR(false, 23001, "获取学习地址失败！");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    private LearningCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
