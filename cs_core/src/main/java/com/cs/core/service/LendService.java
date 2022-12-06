package com.cs.core.service;

import com.cs.core.pojo.entity.BorrowInfo;
import com.cs.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.vo.BorrowInfoApprovalVO;

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
}
