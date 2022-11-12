package com.cs.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.R;
import com.cs.core.pojo.entity.Borrower;
import com.cs.core.pojo.vo.BorrowerApprovalVO;
import com.cs.core.pojo.vo.BorrowerDetailVO;
import com.cs.core.pojo.vo.BorrowerVO;
import com.cs.core.service.BorrowerService;
import com.cs.serviceBase.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("/admin/core/borrower")
@Api(tags = "管理员审批借款")
@Slf4j
public class AdminBorrowerController {

    private BorrowerService borrowerService;
    @Autowired
    public void setBorrowerService(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @ApiOperation("展示借款人信息")
    @GetMapping("list/{page}/{limit}")
    public R<IPage<Borrower>> getBorrower(@PathVariable("page") int currentPage,
                               @PathVariable("limit") int limit,
                               @RequestParam String keyword){

        IPage<Borrower> borrower = borrowerService.getBorrower(currentPage, limit, keyword);
        return R.success(borrower);
    }

    @ApiOperation("展示借款人详细信息")
    @GetMapping("show/{id}")
    public R showDetails(@PathVariable("id") Long id){
        BorrowerDetailVO borrowerDetailVO=borrowerService.showDetails(id);
        return R.success().add("borrowerDetailVO",borrowerDetailVO);
    }
    @ApiOperation("审批借款人认证申请")
    @PostMapping("approval")
    public R approval(@RequestBody BorrowerApprovalVO approvalVO){
        borrowerService.approvalBorrowerAppeal(approvalVO);
        return R.success("审批完成");
    }
}

