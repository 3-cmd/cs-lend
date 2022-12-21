package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.core.mapper.LendItemMapper;
import com.cs.core.mapper.LendItemReturnMapper;
import com.cs.core.mapper.LendMapper;
import com.cs.core.mapper.LendReturnMapper;
import com.cs.core.pojo.entity.Lend;
import com.cs.core.pojo.entity.LendItem;
import com.cs.core.pojo.entity.LendItemReturn;
import com.cs.core.pojo.entity.LendReturn;
import com.cs.core.service.LendItemReturnService;
import com.cs.core.service.UserBindService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {
    @Resource
    private UserBindService userBindService;

    @Resource
    private LendItemMapper lendItemMapper;

    @Resource
    private LendMapper lendMapper;

    @Resource
    private LendReturnMapper lendReturnMapper;
    @Override
    public List<LendItemReturn> selectByLendId(Long lendId, Long userId) {
        LambdaQueryWrapper<LendItemReturn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(LendItemReturn::getLendId, lendId)
                .eq(LendItemReturn::getInvestUserId, userId)
                .orderByAsc(LendItemReturn::getCurrentPeriod);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 添加还款明细
     * @param lendReturnId
     */
    @Override
    public List<Map<String, Object>> addReturnDetail(Long lendReturnId) {

        //获取还款记录
        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        //获取标的信息
        Lend lend = lendMapper.selectById(lendReturn.getLendId());

        //根据还款id获取回款列表
        List<LendItemReturn> lendItemReturnList = this.selectLendItemReturnList(lendReturnId);
        List<Map<String, Object>> lendItemReturnDetailList = new ArrayList<>();
        for(LendItemReturn lendItemReturn : lendItemReturnList) {
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            String bindCode = userBindService.getBindCodeByUserId(lendItem.getInvestUserId());

            Map<String, Object> map = new HashMap<>();
            //项目编号
            map.put("agentProjectCode", lend.getLendNo());
            //出借编号
            map.put("voteBillNo", lendItem.getLendItemNo());
            //收款人（出借人）
            map.put("toBindCode", bindCode);
            //还款金额
            map.put("transitAmt", lendItemReturn.getTotal());
            //还款本金
            map.put("baseAmt", lendItemReturn.getPrincipal());
            //还款利息
            map.put("benifitAmt", lendItemReturn.getInterest());
            //商户手续费
            map.put("feeAmt", new BigDecimal("0"));

            lendItemReturnDetailList.add(map);
        }
        return lendItemReturnDetailList;
    }

    @Override
    public List<LendItemReturn> selectLendItemReturnList(Long lendReturnId) {
        LambdaQueryWrapper<LendItemReturn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LendItemReturn::getLendReturnId, lendReturnId);
        List<LendItemReturn> lendItemReturnList = baseMapper.selectList(wrapper);
        return lendItemReturnList;
    }
}
