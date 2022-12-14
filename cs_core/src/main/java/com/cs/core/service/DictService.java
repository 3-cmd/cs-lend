package com.cs.core.service;

import com.cs.core.pojo.dto.ExcelDictDTO;
import com.cs.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface DictService extends IService<Dict> {
    /**
     * excel文件写入数据库,首先先读取文件,然后通过持久层方法进行数据的解析并写入数据到数据库,导入导出数据针对的是数据库
     *
     * @param is 输入流
     */
    void importData(InputStream is);


    List<ExcelDictDTO> getExcelDictDTOS();

    public List<Dict> listWithTree();

    List<Dict> getListByDictCode(String dictCode);
    String getNameByParentDictCodeAndValue(String dictCode,Integer value);
}
