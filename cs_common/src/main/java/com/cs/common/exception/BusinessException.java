package com.cs.common.exception;

import com.cs.common.result.ResponseEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{
    //错误代码
    private Integer code;
    //错误消息
    private String msg;

    /**
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this.msg = message;
    }

    /**
     *
     * @param message 错误消息
     * @param code 错误码
     */
    public BusinessException(String message, Integer code) {
        this.msg = message;
        this.code = code;
    }

    /**
     *
     * @param message 错误消息
     * @param code 错误码
     * @param cause 原始异常对象
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(cause);
        this.msg = message;
        this.code = code;
    }

    /**
     *
     * @param resultCodeEnum 接收枚举类型
     */
    public BusinessException(ResponseEnum resultCodeEnum) {
        this.msg = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }

    /**
     *
     * @param resultCodeEnum 接收枚举类型
     * @param cause 原始异常对象
     */
    public BusinessException(ResponseEnum resultCodeEnum, Throwable cause) {
        super(cause);
        this.msg = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }

}
