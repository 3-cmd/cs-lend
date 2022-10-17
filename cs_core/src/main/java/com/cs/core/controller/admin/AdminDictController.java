package com.cs.core.controller.admin;


import com.cs.common.exception.BusinessException;
import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import com.cs.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Api(tags = "数据字典管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/dict")
public class AdminDictController {
    @Autowired
    private DictService dictService;


    @PostMapping("importExcelToDataBase")
    @ApiOperation("excel文件的批量导入")
    public R importExcelToDataBase(
            @RequestParam
            @ApiParam("excel的输入流文件")
            MultipartFile multipartFile){
        try {
            dictService.importData(multipartFile.getInputStream());
            return R.success("数据批量导入成功");
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }
}

