package com.readyidu.robot.api.exception;

/**
 * @author wlq
 * @Description 自定义ApiException
 * @date 2017/1/22
 * @modify wlq
 * @modify_date 2017/6/29
 */
public class BaseApiException extends Throwable {

    private final int code;
    private final String message;

    public BaseApiException(int errCode, String errMsg) {
        this.code = errCode;
        this.message = errMsg;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
