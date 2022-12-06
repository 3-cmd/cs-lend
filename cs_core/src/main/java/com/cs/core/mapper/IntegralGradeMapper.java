package com.cs.core.mapper;

import com.cs.core.pojo.entity.IntegralGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
 * <p>
 * 积分等级表 Mapper 接口
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Mapper
public interface IntegralGradeMapper extends BaseMapper<IntegralGrade> {
    public Integer selectMaxIntegralAmount();
}
