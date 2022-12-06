package com.cs.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.common.result.R;
import com.cs.core.pojo.entity.BorrowInfo;
import com.cs.core.pojo.vo.BorrowInfoApprovalVO;
import com.cs.core.service.BorrowInfoService;
import com.cs.serviceBase.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/admin/core/borrowInfo")
@Api(tags = "借款信息")
@Slf4j
public class AdminBorrowInfoController {
    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("借款信息列表展示")
    @GetMapping("list/{page}/{limit}")
    public R list(@PathVariable("page") Long currentPage,@PathVariable Long limit,@RequestParam String keyword){
        Page<BorrowInfo> page = borrowInfoService.getBorrowerInfoList(currentPage, limit, keyword);
        return R.success(page);
    }
    @ApiOperation("借款信息详情")
    @GetMapping("show/{id}")
    public R show(@ApiParam(value = "借款信息id") @PathVariable Long id){
        Map<String,Object> borrowInfoDetail=borrowInfoService.getBorrowInfoDetails(id);
        return R.success().add("borrowInfoDetail",borrowInfoDetail);
    }
    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO) {
        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.success("审批完成");
    }
}