package com.cs.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.core.pojo.entity.BorrowInfo;
import com.cs.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    Page<Lend> getByPageList(Long currentPage, Long pageSize, String keyword);

    Map<String, Object> getLendDetail(Long id);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    void makeLoan(Long id);
}
