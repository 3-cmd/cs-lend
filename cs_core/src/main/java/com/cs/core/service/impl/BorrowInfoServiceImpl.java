package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.common.exception.Assert;
import com.cs.common.result.ResponseEnum;
import com.cs.core.enums.BorrowAuthEnum;
import com.cs.core.enums.BorrowInfoStatusEnum;
import com.cs.core.enums.BorrowerStatusEnum;
import com.cs.core.enums.UserBindEnum;
import com.cs.core.mapper.IntegralGradeMapper;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.pojo.entity.BorrowInfo;
import com.cs.core.mapper.BorrowInfoMapper;
import com.cs.core.pojo.entity.IntegralGrade;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.serviceBase.utils.JwtUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private BorrowInfoMapper borrowInfoMapper;

    /**
     * 根据当前登录的用户进行借款额度的获取---> 首先user_info表中获取该用户积分,然后integral_grade表中获取对应的最大借款额度
     * @param request 根据请求头获取token
     * @return 返回获取到的申请额度
     */
    @Override
    public BigDecimal getBorrowAmount(HttpServletRequest request) {
        //1.获取借款人的积分
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();
        //2.根据借款人积分获取到最大的额度
        LambdaQueryWrapper<IntegralGrade> wrapper=new LambdaQueryWrapper<>();
        //sql--->  where integralStart<=integral and IntegralEnd>= integral;
        wrapper.le(IntegralGrade::getIntegralStart,integral)
                .ge(IntegralGrade::getIntegralEnd,integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(wrapper);
        //如果用户的综合积分大于积分登记表的最高积分,那么该用户可以贷款最高额度
        Integer max=integralGradeMapper.selectMaxIntegralAmount();
        wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(IntegralGrade::getIntegralEnd,max);
        IntegralGrade maxIntegralGround = integralGradeMapper.selectOne(wrapper);
        BigDecimal maxAmount = maxIntegralGround.getBorrowAmount();
        if (integral>max) return maxAmount;
        //如果无法查询出来并且该用户的积分值没有大于最大积分组,那么该用户没有积分则无法借款
        if (integralGrade==null) return new BigDecimal("0");
        return integralGrade.getBorrowAmount();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId,HttpServletRequest request) {
        //为了程序的健壮性,应该判断借款人的账户绑定状态和借款申请状态
        UserInfo userInfo = userInfoMapper.selectById(userId);
        //账户绑定状态
        Assert.equals(userInfo.getBindStatus(), UserBindEnum.BIND_OK.getStatus(), ResponseEnum.USER_NO_BIND_ERROR);
        //借款人额度的申请状态的判断
        Assert.equals(userInfo.getBorrowAuthStatus(), BorrowerStatusEnum.AUTH_OK.getStatus(), ResponseEnum.USER_NO_AMOUNT_ERROR);
        //查询当前登陆的用户的最大额度
        BigDecimal borrowAmount = this.getBorrowAmount(request);
        //判断借款人的额度是否充足
        Assert.isTrue(borrowInfo.getAmount().doubleValue()<=borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);
        //处理数据
        borrowInfo.setUserId(userId);
        //将获取的百分数转为小数
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        //设置当前申请的状态
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        //存储数据到borrower_info
        baseMapper.insert(borrowInfo);

    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        LambdaQueryWrapper<BorrowInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(BorrowInfo::getUserId,userId);
        BorrowInfo borrowInfo = borrowInfoMapper.selectOne(wrapper);
        //如果没有查询出来,那么该用户没有提交借款申请
        if (borrowInfo==null) return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        return borrowInfo.getStatus();
    }
}
