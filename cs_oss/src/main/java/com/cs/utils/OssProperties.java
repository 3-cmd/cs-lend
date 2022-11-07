package com.cs.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
public class OssProperties  {

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
    @Value("${spring.cloud.alicloud.access-key}")
    private String keyId;
    @Value("${spring.cloud.alicloud.secret-key}")
    private String keySecret;
    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucketName;

    public static String ENDPOINT;

    public static String KEY_ID;

    public static String KEY_SECRET;

    public static String BUCKET_NAME;

    //当私有成员被赋值后，此方法自动被调用，从而初始化常量
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        ENDPOINT = endpoint;
//        KEY_ID = keyId;
//        KEY_SECRET = keySecret;
//        BUCKET_NAME = bucketName;
//    }
}