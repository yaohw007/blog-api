package cn.poile.blog.controller;

import cn.poile.blog.common.constant.ErrorEnum;
import cn.poile.blog.common.response.ApiResponse;
import org.springframework.validation.annotation.Validated;

/**
 * @author: yaohw
 * @create: 2019-10-23 12:36
 **/
@Validated
public class BaseController {

    private <T> ApiResponse<T> init() {
        return new ApiResponse<>();
    }

    protected <T> ApiResponse<T> createResponse() {
        ApiResponse<T> response = init();
        response.setCode(ErrorEnum.SUCCESS.getErrorCode());
        response.setMessage(ErrorEnum.SUCCESS.getErrorMsg());
        return response;
    }

    protected <T> ApiResponse<T> createResponse(T body) {
        ApiResponse<T> response = createResponse();
        response.setData(body);
        return response;
    }
}
