package com.cs.core.service;

import com.cs.core.pojo.bo.TransFlowBO;
import com.cs.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface TransFlowService extends IService<TransFlow> {

    void saveTransFlow(TransFlowBO transFlowBO);
    boolean isHaveTransFlow(String agentBillNo);

    List<TransFlow> selectByUserId(Long userId);
}
