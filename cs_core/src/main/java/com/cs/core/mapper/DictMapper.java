package com.cs.core.mapper;

import com.cs.core.pojo.dto.ExcelDictDTO;
import com.cs.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface DictMapper extends BaseMapper<Dict> {

    void saveBatch(@Param("dictList") List<ExcelDictDTO> dictList);
}
