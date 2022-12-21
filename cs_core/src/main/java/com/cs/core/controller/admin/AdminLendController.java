package com.cs.core.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.common.result.R;
import com.cs.core.pojo.entity.Lend;
import com.cs.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@RestController
@RequestMapping("admin/core/lend")
@Api(tags = "管理员标的管理")
@Slf4j
public class AdminLendController {
    @Resource
    private LendService lendService;

    @GetMapping("list/{page}/{limit}")
    @ApiOperation("标的分页展示")
    public R list(@PathVariable("page") Long currentPage ,
                  @PathVariable("limit") Long pageSize,
                  @RequestParam String keyword){
        Page<Lend> page=lendService.getByPageList(currentPage,pageSize,keyword);
        return R.success(page);
    }
    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long id) {
        Map<String, Object> result = lendService.getLendDetail(id);
        return R.success().add("lendDetail", result);
    }
    @ApiOperation("放款")
    @GetMapping("/makeLoan/{id}")
    public R makeLoan(
            @ApiParam(value = "标的id", required = true)
            @PathVariable("id") Long id) {
        lendService.makeLoan(id);
        return R.success("放款成功");
    }
}

