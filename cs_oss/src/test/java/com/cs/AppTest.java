package com.cs;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
    @Value("${spring.cloud.alicloud.access-key}")
    private String keyId;
    @Value("${spring.cloud.alicloud.secret-key}")
    private String keySecret;
    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucketName;

    @Test
    public void test1() {
        String email = "1397368928@qq.com";
        System.out.println(email.substring(0, email.indexOf("@")));
        int x=1233;
    }
    @Test
    public void test2(){
        System.out.println(endpoint);
        System.out.println(bucketName);
        System.out.println(keyId);
        System.out.println(keySecret);
    }
}


