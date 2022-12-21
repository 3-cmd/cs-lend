package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.common.exception.Assert;
import com.cs.common.result.ResponseEnum;
import com.cs.core.csb.FormHelper;
import com.cs.core.csb.HfbConst;
import com.cs.core.csb.RequestHelper;
import com.cs.core.enums.TransTypeEnum;
import com.cs.core.mapper.UserAccountMapper;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.pojo.bo.TransFlowBO;
import com.cs.core.pojo.entity.UserAccount;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.service.TransFlowService;
import com.cs.core.service.UserAccountService;
import com.cs.core.service.UserBindService;
import com.cs.core.utils.LendNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    @Resource
    private UserBindService userBindService;

    @Resource
    private UserAccountService userAccountService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private TransFlowService transFlowService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //判断账户绑定状态
        Assert.notEmpty(bindCode, ResponseEnum.USER_NO_BIND_ERROR);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);
        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {
        //商户充值订单号在同一充值请求的多次尝试下是一致的,也就是说当网络出现短暂故障时订单号是一致的,避免痴线重复添加的问题,采用了唯一索引,并且为了健壮性的代码考量,需要判断多次充值的订单号是否存在与数据库中
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean result = transFlowService.isHaveTransFlow(agentBillNo);
        //如果存在相同的流水号,返回success,表示告知第三方不需要再次重新发送请求,不需要再重试
        if (result) {
            return "success";
        }

        //log.info("充值成功：" + JSONObject.toJSONString(paramMap));
        String bindCode = (String) paramMap.get("bindCode"); //充值人绑定协议号
        String chargeAmt = (String) paramMap.get("chargeAmt"); //充值金额

        //通过csb中user_account表中的user_code对应到user_info表中的bind_code,来查询到userId
        //优化,根据第三方服务返回的数据,拿到bindCode(因为第三方数据没有userid,所以通过bindCode来查询userId),修改userAccount表中的数据
        //第二个参数为充值的金额,第三个数据为冻结的金额
        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));

        //增加交易流水,在充值之后,增加交易流水的记录,将数据添加到trans_flow表中
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);
        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {
        LambdaQueryWrapper<UserAccount> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserAccount::getUserId,userId);
        UserAccount userAccount = baseMapper.selectOne(wrapper);
        if (userAccount!=null)
            return userAccount.getAmount();
        else return null;
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {

        //账户可用余额充足：当前用户的余额 >= 当前用户的提现金额
        BigDecimal amount = userAccountService.getAccount(userId);//获取当前用户的账户余额
        Assert.isTrue(amount.doubleValue() >= fetchAmt.doubleValue(),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);


        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {

        log.info("提现成功");
        String agentBillNo = (String)paramMap.get("agentBillNo");
        boolean result = transFlowService.isHaveTransFlow(agentBillNo);
        if(result){
            log.warn("幂等性返回");
            return;
        }

        String bindCode = (String)paramMap.get("bindCode");
        String fetchAmt = (String)paramMap.get("fetchAmt");

        //根据用户账户修改账户金额
        baseMapper.updateAccount(bindCode, new BigDecimal("-" + fetchAmt), new BigDecimal(0));

        //增加交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现");
        transFlowService.saveTransFlow(transFlowBO);
    }
}
