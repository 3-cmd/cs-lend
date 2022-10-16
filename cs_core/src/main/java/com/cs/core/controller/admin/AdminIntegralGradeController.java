package com.cs.core.controller.admin;

import com.cs.common.exception.Assert;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import com.cs.core.pojo.entity.IntegralGrade;
import com.cs.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 积分等级表 后台管理员
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Api(tags = "积分等级表")
@RestController//@ResponseBody+@Controller
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {
    @Autowired
    private IntegralGradeService integralGradeService;

    //使用restful风格的请求方式
    @GetMapping("list")
    @ApiOperation("获取积分等级")
    public R<List<IntegralGrade>> getList() {
        return R.success("返回积分等级列表", integralGradeService.list());
    }

    @ApiOperation("根据id删除积分等级")
    @DeleteMapping("remove/{id}")
    public R removeBuId(
            @ApiParam("数据的id")
            @PathVariable Long id) {
        boolean b = integralGradeService.removeById(id);
        if (!b) return R.error();
        return R.success("删除成功");
    }
    @ApiOperation("积分等级的添加")
    @PutMapping("save")
    public R save(
            @ApiParam("积分等级对象")
            @RequestBody IntegralGrade integralGrade){
        boolean save = integralGradeService.save(integralGrade);
        if (!save) return R.error();
        //断言工具类,封装了if语句表示如果传入的参数不能为null
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        return R.success("添加成功");
    }
    @ApiOperation("积分等级根据id来查询")
    @GetMapping("getById/{id}")
    public R getById(
            @ApiParam("数据id")
            @PathVariable Long id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade!=null) return R.success("获取成功",integralGrade);
        return R.error("获取失败");
    }
    @ApiOperation("更新积分等级")
    @PutMapping("update")
    public R update(
            @ApiParam("积分等级对象")
            @RequestBody IntegralGrade integralGrade){
        boolean b = integralGradeService.updateById(integralGrade);
        if (!b) return R.error("更新失败");
        return R.success("更新成功");
    }
}
