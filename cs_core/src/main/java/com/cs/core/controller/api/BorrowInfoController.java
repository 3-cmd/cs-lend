package com.cs.core.controller.api;


import com.cs.common.result.R;
import com.cs.core.pojo.entity.BorrowInfo;
import com.cs.core.service.BorrowInfoService;
import com.cs.serviceBase.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/api/core/borrowInfo")
@Api(tags = "借款信息")
@Slf4j
public class BorrowInfoController {
    @Resource
    private BorrowInfoService borrowInfoService;

    /**
     * 根据当前登录的用户进行借款额度的获取---> 首先user_info表中获取该用户积分,然后integral_grade表中获取对应的最大借款额度
     * @param request 根据请求头获取token
     * @return 返回获取到的申请额度
     */
    @ApiOperation("获取借款额度")
    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){
        BigDecimal borrowAmount=borrowInfoService.getBorrowAmount(request);
        return R.success(borrowAmount);
    }
    @ApiOperation("提交借款申请")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request) {
        //首先获取到当前的登录用户
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowInfoService.saveBorrowInfo(borrowInfo,userId,request);
        return R.success("提交成功");
    }
    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowInfoService.getStatusByUserId(userId);
        return R.success().add("borrowInfoStatus",status);
    }
}