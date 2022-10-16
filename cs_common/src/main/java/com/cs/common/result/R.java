package com.cs.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@Data
public class R<T> implements Serializable {
//    private Integer code;
//    private String message;
//    private Map<String, Object> data=new HashMap<>();
//    private R(){}
//    //返回成功的结果
//    public static R ok(){
//        R r=new R();
//        r.setCode(ResponseEnum.SUCCESS.getCode());
//        r.setmessage(ResponseEnum.SUCCESS.getmessage());
//        return r;
//    }
//    //返回失败的结果
//    public static R error(){
//        R r=new R();
//        r.setCode(ResponseEnum.ERROR.getCode());
//        r.setmessage(ResponseEnum.ERROR.getmessage());
//        return r;
//    }
//    public static R setResult(ResponseEnum responseEnum){
//
//    }
    /**
     * 返回的数据最后都会封转成此对象
     *
     * @param <T>
     */

    private Integer code; //编码：0成功，-1和其它数字为失败

    private String message; //错误信息

    private T data; //数据

    private Map<String,Object> map = new HashMap<>(); //动态数据

    //构造私有化,外部无法获取r对象
    private R(){}

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = ResponseEnum.SUCCESS.getCode();
        r.message=ResponseEnum.SUCCESS.getMessage();
        return r;
    }
    public static <T> R<T> success() {
        R<T> r = new R<T>();
        r.code = ResponseEnum.SUCCESS.getCode();
        r.message=ResponseEnum.SUCCESS.getMessage();
        return r;
    }
    public static <T> R<T> success(String message) {
        R<T> r = new R<T>();
        r.code = ResponseEnum.SUCCESS.getCode();
        r.message=message;
        return r;
    }
    public static <T> R<T> success(String message,T object) {
        R<T> r = new R<>();
        r.data = object;
        r.message = message;
        r.code = ResponseEnum.SUCCESS.getCode();
        return r;
    }

    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.message = message;
        r.code = ResponseEnum.ERROR.getCode();
        return r;
    }
    public static <T> R<T> error(Integer code,String message) {
        R<T> r = new R<>();
        r.code=code;
        r.message = message;
        return r;
    }
    public static <T> R<T> error() {
        R<T> r = new R<>();
        r.message = ResponseEnum.ERROR.getMessage();
        r.code = ResponseEnum.ERROR.getCode();
        return r;
    }
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
