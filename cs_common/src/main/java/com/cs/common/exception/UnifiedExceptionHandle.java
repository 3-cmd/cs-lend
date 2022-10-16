package com.cs.common.exception;

import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 统一异常处理器
 */
@Slf4j
@ControllerAdvice//异常切面
@ResponseBody
public class UnifiedExceptionHandle {

    //    @ExceptionHandler(value = HttpMessageNotReadableException.class)
//    public R handleException(HttpMessageNotReadableException e){
//        log.error(e.getMessage(),e);
//        return R.error("json数据可能传递错误");
//    }

    /**
     * Controller上一层相关异常
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public R handleServletException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error(ResponseEnum.SERVLET_ERROR.getCode(), ResponseEnum.SERVLET_ERROR.getMessage());
    }

//    @ExceptionHandler(value = Exception.class)
//    public R handleException(Exception e) {
//        log.error(e.getMessage(), e);
//        return R.error();
//    }

    @ExceptionHandler(value = BusinessException.class)
    public R handleException(BusinessException be) {
        log.error(be.getMessage(), be);
        return R.error(be.getCode(), be.getMsg());
    }


}
