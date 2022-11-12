package com.cs.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cs.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.vo.BorrowerApprovalVO;
import com.cs.core.pojo.vo.BorrowerDetailVO;
import com.cs.core.pojo.vo.BorrowerVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrowersByUserId(BorrowerVO borrowerVO, Long userId);

    Integer getBorrowerStatus(HttpServletRequest request);

    IPage<Borrower> getBorrower(int currentPage, int limit, String keyword);

    BorrowerDetailVO showDetails(Long id);

    void approvalBorrowerAppeal(BorrowerApprovalVO approvalVO);
}
