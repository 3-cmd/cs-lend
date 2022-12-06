package com.cs.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.vo.BorrowInfoApprovalVO;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    Page<BorrowInfo> getBorrowerInfoList(Long currentPage, Long limit, String keyword);

    Map<String, Object> getBorrowInfoDetails(Long id);

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
