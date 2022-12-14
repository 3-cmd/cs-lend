package com.cs.core.service;

import com.cs.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmount(HttpServletRequest request);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId,HttpServletRequest request);

    Integer getStatusByUserId(Long userId);
}
