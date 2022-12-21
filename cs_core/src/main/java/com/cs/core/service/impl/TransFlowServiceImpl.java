package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.core.pojo.bo.TransFlowBO;
import com.cs.core.pojo.entity.TransFlow;
import com.cs.core.mapper.TransFlowMapper;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.service.TransFlowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.core.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        //获取userId
        String bindCode = transFlowBO.getBindCode();
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getBindCode,bindCode);
        UserInfo userInfo = userInfoService.getOne(wrapper);
        //Long userId=userInfo.getId();

        TransFlow transFlow = new TransFlow();
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransNo(transFlowBO.getAgentBillNo());//流水号
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        baseMapper.insert(transFlow);
    }

    /**
     * 判断是否在存在相同的订单号
     * @param agentBillNo 流水号---订单号
     * @return 存在为true
     */
    @Override
    public boolean isHaveTransFlow(String agentBillNo) {
        LambdaQueryWrapper<TransFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransFlow::getTransNo,agentBillNo);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0 ;
    }

    @Override
    public List<TransFlow> selectByUserId(Long userId) {
        LambdaQueryWrapper<TransFlow> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(TransFlow::getUserId,userId).orderByDesc(TransFlow::getId);
        return baseMapper.selectList(wrapper);
    }
}
