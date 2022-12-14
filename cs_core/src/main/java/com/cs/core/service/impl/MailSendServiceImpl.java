package com.cs.core.service.impl;

import com.cs.common.exception.Assert;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.ResponseEnum;
import com.cs.core.service.MailSendService;
import com.cs.core.service.UserInfoService;
import com.cs.core.utils.MailUtil;
import com.cs.core.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码发送的控制层
 */
@Service
@Slf4j
public class MailSendServiceImpl implements MailSendService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void send(String eMail, String mobile) {
        //当我们点击发送器验证码的时候就应该判断此手机号是否被注册了,这样就不需要发送验证码来减少麻烦
        //如果改结果返回为false,代表已经被注册了 为true则代表未被注册
        boolean result = userInfoService.checkMobile(mobile);
        if (!result) throw new BusinessException(ResponseEnum.MOBILE_EXIST_ERROR);
        //判断邮箱是否正确
        Assert.isTrue(eMail.matches("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?"), ResponseEnum.EMAIL_ERROR);
        //redis中存储的键名称,自定义前缀加上当前账户登录的email
        String key = MailUtil.MAIL_PREFIX + eMail;
        String code = String.valueOf(ValidateCodeUtils.generateValidateCode(6));
        log.info("验证码:{}", code);
        StringBuilder sb = new StringBuilder("你好,你的验证码为:");
        sb.append(code).append(",请你及时使用,将在5分钟后失效,本验证码只用于注册网站 ");
        String text = sb.toString();
        boolean sendFlag = MailUtil.sendMail(eMail, text, "cs_lend借贷平台");
        //判断发送的结果,如果结果为false,那么抛出异常
        Assert.isTrue(sendFlag, ResponseEnum.ALIYUN_SMS_ERROR);

        redisTemplate.opsForValue().set(key, code, 1000 * 5 * 60, TimeUnit.MINUTES);
        //throw new BusinessException(ResponseEnum.ERROR);

    }
}
