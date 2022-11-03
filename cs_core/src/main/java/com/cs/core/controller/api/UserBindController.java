package com.cs.core.controller.api;


import com.cs.common.result.R;
import com.cs.core.csb.RequestHelper;
import com.cs.core.pojo.vo.UserBindVO;
import com.cs.core.service.UserBindService;
import com.cs.serviceBase.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/api/core/userBind")
@Api("会员账号绑定")
@Slf4j
public class UserBindController {

    @Autowired
    private UserBindService userBindService;
    @ApiOperation("账户半丁提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){
        //当我们登录时,前端会将我们生成的token进行存储,当我们登陆后,每次发送的请求都会携带一个token,会在请求头之中
        //获取token进行解析,可以知道是哪一个用户登录,我们将这个登录的用户进行账户的绑定即可
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        //根据userid进行账户的绑定,由于规定我们需要动态生成表单数据然后提交请求,所以这里的返回值为一个表单
        //这个表单设置对应的属性,当我们将这个字符串的表单返回前端后,这个表单可以自动提交,来发送请求
        String form=userBindService.commitBind(userBindVO,userId);
        return R.success().add("form",form);
    }

    /**
     * 账户绑定完成后,csb会根据发送过去的数据进行访问这个接口,并且文档规定这个返回值必须为String类型
     * @param request
     * @return
     */
    @ApiOperation("账户绑定成功后的回调函数")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        //csb发起回调传回的参数,其中会携带我么发送过去的签名,再次把签名sign传回来进行校验
        Map<String, Object> paraMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("账户绑定数据发起回调传回的参数:{}",paraMap);
        //进行签名的校验
        if (!RequestHelper.isSignEquals(paraMap)) {
            log.error("用户账号绑定异步回调签名错误{}",paraMap);
            return "error";//返回非success的字符串即可
        }
        log.info("验签成功,开始账户绑定");
        userBindService.notify(paraMap);
        return "success";
    }

}

