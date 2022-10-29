package com.cs.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cs.common.exception.Assert;
import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.pojo.entity.UserLoginRecord;
import com.cs.core.pojo.query.UserInfoQuery;
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
import java.util.List;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/admin/core/userInfo")
@Api(tags = "会员管理")
public class AdminUserInfoController {


    @Autowired
    private UserInfoService userInfoService;

    //page代表第几页
    //limit代表每页记录数
    @ApiOperation("会员列表查询")
    @GetMapping("/list/{page}/{limit}")
    public R list(
            @ApiParam("第几页")
            @PathVariable(value = "page",required = true) Long page,
            @ApiParam("每页记录数")
            @PathVariable(value = "limit",required = true) Long limit,
            @ApiParam(value = "查询条件" ,required = false) UserInfoQuery userInfoQuery){
        IPage<UserInfo> iPage = userInfoService.listPage(page, limit, userInfoQuery);
        return R.success(iPage);
    }
    @ApiOperation("会员账户锁定与解锁按钮")
    @PutMapping("/lock/{id}/{status}")
    public R lock(@ApiParam("要修改的会员的id") @PathVariable("id") Long id,
                  @ApiParam("要修改的会员的状态") @PathVariable("status") Integer status){
        int result = userInfoService.updateLock(id, status);
        if (result>0) return R.success("修改成功");
        return R.error("修改失败");
    }
    @ApiOperation("会员登录日志的查询")
    @GetMapping("/getlog/{userId}")
    public R<List<UserLoginRecord>> getLog(@PathVariable("userId") Long userId ){
        List<UserLoginRecord> list=userInfoService.getLog(userId);
        return R.success(list);
    }
}

