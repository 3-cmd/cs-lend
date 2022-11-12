package com.cs.core.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.common.exception.Assert;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import com.cs.core.pojo.entity.Borrower;
import com.cs.core.pojo.vo.BorrowerVO;
import com.cs.core.service.BorrowerService;
import com.cs.serviceBase.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/api/core/borrower")
@Api(tags = "借款人")
@Slf4j
public class BorrowerController {

    @Resource
    private BorrowerService borrowerService;
    @ApiOperation("保存借款人信息")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowerVO borrowerVO, HttpServletRequest request){
        //首先判断用户是否登陆,如果未登录,则使用户返回到登录界面,如果没有获取到token,那么证明位登陆
        String token=request.getHeader("token");
        if (token==null) throw new BusinessException("用户未登录,请先登录");
        Long userId = JwtUtils.getUserId(token);
        borrowerService.saveBorrowersByUserId(borrowerVO,userId);
        return R.success("信息提交成功");
    }

    @ApiOperation("获取登录用户的借贷状态")
    @GetMapping("auth/getBorrowerStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        Integer status = borrowerService.getBorrowerStatus(request);
        return R.success(status);
    }

}

