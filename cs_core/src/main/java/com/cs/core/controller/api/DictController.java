package com.cs.core.controller.api;

import com.cs.common.result.R;
import com.cs.core.pojo.entity.Dict;
import com.cs.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api("数据字典")
@RestController
@RequestMapping("api/core/dict")
@Slf4j
public class DictController {
    @Resource
    private DictService dictService;

    @ApiOperation("个面具dictcode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public R findByDictCode(@ApiParam(value = "节点编码", required = true)
                                @PathVariable String dictCode){

        List<Dict> list = dictService.getListByDictCode(dictCode);
        return R.success("查询成功",list);
    }
}
