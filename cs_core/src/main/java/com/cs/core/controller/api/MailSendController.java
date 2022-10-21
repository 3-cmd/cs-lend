package com.cs.core.controller.api;

import com.cs.common.result.R;
import com.cs.core.service.MailSendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/core/send")
@Api(tags = "邮箱验证码管理")
@Slf4j
public class MailSendController {
    @Resource
    private MailSendService mailSendService;
    @GetMapping("send/{email}")
    @ApiOperation("邮箱验证码发送方法")
    public R send(@PathVariable("email")
                    @ApiParam("邮箱")
                      String email){
        mailSendService.send(email);
        return R.success("发送成功");
    }
}
