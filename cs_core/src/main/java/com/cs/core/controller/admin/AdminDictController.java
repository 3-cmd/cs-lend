package com.cs.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.R;
import com.cs.common.result.ResponseEnum;
import com.cs.core.pojo.dto.ExcelDictDTO;
import com.cs.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

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


    /**
     *
     * @param file 前端传入的参数名称必须与此名称相同
     * @return
     */
    @PostMapping("importExcelToDataBase")
    @ApiOperation("excel文件的批量导入")
    public R importExcelToDataBase(
            @RequestParam("file")
            @ApiParam("excel的输入流文件")
            MultipartFile file){
        try {
            dictService.importData(file.getInputStream());
            return R.success("数据批量导入成功");
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }
    @ApiOperation("excel文件的批量导出")
    @GetMapping("exportExcelFromDataBase")
    public void exportExcelDataBase(HttpServletResponse response) {
        try {
//            response.setContentType("application/vnd.ms-excel");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.getExcelDictDTOS());
        } catch (IOException e) {
            //EXPORT_DATA_ERROR(104, "数据导出失败"),
            throw new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }

    }
}

