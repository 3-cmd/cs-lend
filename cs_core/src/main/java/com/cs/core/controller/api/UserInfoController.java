package com.cs.core.controller.api;


import com.cs.common.exception.Assert;
import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.pojo.vo.LoginVO;
import com.cs.core.pojo.vo.RegisterVO;
import com.cs.core.pojo.vo.UserInfoVO;
import com.cs.core.service.UserInfoService;
import com.cs.core.utils.MailUtil;
import com.cs.serviceBase.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/api/core/userInfo")
@Api(tags = "会员接口")
public class UserInfoController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public R register(
            @ApiParam("用户信息")
            @RequestBody
            RegisterVO register){

        //获取
        String key= MailUtil.MAIL_PREFIX+register.getEmail();
        Integer value = (Integer) redisTemplate.opsForValue().get(key);
        //校验验证码是否正确
        Assert.equals(register.getCode(),String.valueOf(value), ResponseEnum.CODE_ERROR);
        //注册
        userInfoService.register(register);
        return R.success("注册成功");
    }
    @ApiOperation("会员登录")
    @PostMapping("/login")
    public R<UserInfoVO> login(@RequestBody LoginVO loginVO, HttpServletRequest request){
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Assert.notEmpty(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);
        String ip = request.getRemoteAddr();
        UserInfoVO userInfoVO=userInfoService.login(loginVO,ip);
        return R.success(userInfoVO);
    }
    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);
        if(result){
            return R.success();
        }else{
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return R.error(ResponseEnum.LOGIN_AUTH_ERROR.getCode(),ResponseEnum.LOGIN_AUTH_ERROR.getMessage());
        }
    }
    @ApiOperation("检查手机号是否存在")
    @GetMapping("/checkMobile/{mobile}")
    public R checkMobile(@PathVariable("mobile") String mobile) {
        boolean result=userInfoService.checkMobile(mobile);
        return R.success(result);
    }
}

