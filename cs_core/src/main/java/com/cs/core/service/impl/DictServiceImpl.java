package com.cs.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.ResponseEnum;
import com.cs.core.listener.ExcelDictListener;
import com.cs.core.pojo.dto.ExcelDictDTO;
import com.cs.core.pojo.entity.Dict;
import com.cs.core.mapper.DictMapper;
import com.cs.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictMapper dictMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream is) {
        EasyExcel.read(is, ExcelDictDTO.class, new ExcelDictListener(dictMapper)).sheet().doRead();
        log.info("Excel导入成功");
    }

    /**
     * 获取数据库中的数据并且拷贝到excel对应的实体类中
     * @param
     * @return
     */
    @Override
    public List<ExcelDictDTO> getExcelDictDTOS() {
        List<Dict> list = this.list();
        List<ExcelDictDTO> excelDictDTOS = list.stream().map(iter -> {
            ExcelDictDTO excelDictDTO= new ExcelDictDTO();
            BeanUtils.copyProperties(iter, excelDictDTO);
            return excelDictDTO;
        }).collect(Collectors.toList());
        return excelDictDTOS;
    }

}
