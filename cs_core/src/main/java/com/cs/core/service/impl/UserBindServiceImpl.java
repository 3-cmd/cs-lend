package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.common.exception.Assert;
import com.cs.common.result.ResponseEnum;
import com.cs.core.csb.FormHelper;
import com.cs.core.csb.HfbConst;
import com.cs.core.csb.RequestHelper;
import com.cs.core.enums.UserBindEnum;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.pojo.entity.UserBind;
import com.cs.core.mapper.UserBindMapper;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.pojo.vo.UserBindVO;
import com.cs.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserBindMapper userBindMapper;


    /**
     * 由于接口中的规定,我们需要使用表单来发送请求,所以这里动态的生成表单
     * 生成动态表单字符串,根据这个表单来发送请求,这个表单中拼接了自动提交的属性,当我们返回这个字符串表单时,就可以自动发送请求了
     *
     * @param userBindVO 用户绑定的具体数据
     * @param userId     当前登录用户的id
     * @return 返回动态生成的表单
     */
    @Override
    public String commitBind(UserBindVO userBindVO, Long userId) {
        /*
         *进行数据的写入,这里当我们点击前端页面的开户时,是不会调用此请求的,只有确认开会才会调用此请求,所以将这个数据的写入定义在这里
         *也是合适的,并且再写入时应该判断是否已经进行了绑定,如此看来,业务逻辑是可以说的通的
         */

        //如果我们发现了不同的UserId使用了相同的身份证,那我们应该制止这种情况,因为一个人只能申请一个账号并且一个人不能既是借款人又是贷款人这样对双方都存在风险,
        //根据传递过来的idCard来查询数据库,如果查询到的数据中的userId与我前端传递过来的userId不同,那么抛出异常并且终止往下执行
        //但是如果是相同userId的那么我们只需要跟新新的信息即可
        LambdaQueryWrapper<UserBind> userBindWrapper = new LambdaQueryWrapper<>();
        userBindWrapper.eq(UserBind::getIdCard,userBindVO.getIdCard())
                //ne 代表 not equals :即userId不相等
                .ne(UserBind::getUserId,userId);
        UserBind one = baseMapper.selectOne(userBindWrapper);
        //如果查询出来数据,则证明违反了规定,那么抛出异常,此处为断言函数,我们断言他是null,如果不是null,那么抛出异常
        Assert.isNull(one, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);
        //用户是否曾经填写过绑定的表单,user_info与user_bind的关系是一对一的,一个登陆的账户只能绑定一个账户
        LambdaQueryWrapper<UserBind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBind::getUserId, userId);
        UserBind userBind = userBindMapper.selectOne(wrapper);
        if (userBind == null) {
            //用户未曾填写过
            //开始创建user_bind记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            //为什么这里填写的是NO_BIND呢,因为绑定的完成还需要第三方服务的回调求情的成功,才能正真的算完成绑定
            //当回调函数发送过来会携带参数,生成bind_code等等一系列数据,到时候才算正真的绑定成功
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        } else {
            //用户填写过但是没有确认绑定:即相同的userid进行了多次填写,那么取出来进行跟新操作即可
            BeanUtils.copyProperties(userBindVO, userBind);
            userBindMapper.updateById(userBind);
        }

//        String form="<!DOCTYPE html>\n" +
//                "<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
//                "  <head> </head>\n" +
//                "  <body>\n" +
//                "    <form name=\"form\" action=\"http://localhost:9999/userBind/BindAgreeUserV2\" method=\"post\">\n" +
//                "      <!-- 参数 -->\n" +
//                "      <input type=\"text\" name=\"mobile\" value=\""+userBindVO.getMobile()+"\" />\n" +
//                "      <input type=\"text\" name=\"sign\" value=\"123456\" />\n" +
//                "    </form>\n" +
//                "    <script>\n" +
//                "      document.form.submit()\n" +
//                "    </script>\n" +
//                "  </body>\n" +
//                "</html>";
        //通过工具包来生成上面的表单
        HashMap<String, Object> paramMap = new HashMap<>();
        //将需要传递的参数交给表单,这里是接口规定的参数,我们只需要按照规定给就是了
        /*
        字段名	         类型	   长度	 必输	 说明
        agentId 	     int		      是	给商户分配的唯一标识
        agentUserId 	string	    50	  是	商户的个人会员ID。由数字、字母组成。
        idCard	        string	    18	  是	身份证号
        personalName	string	    50	  是	真实姓名。由2~5个汉字组成。
        bankType	    string	    10	  是	银行卡类型
        bankNo	        string	    20	  是	银行卡
        mobile	        string	    11	  是	银行卡预留手机
        email	        string		      是	邮箱.如果商户对接理财业务，必须传该参数. 根据当前账户对象来获取该值即可
        returnUrl	    string	    255	  是	绑定完成后同步返回商户的完整地址。
        notifyUrl	    string	    255   是	绑定完成后异步通知商户的完整地址。
        timestamp	    long		      是	时间戳。从1970-01-01 00:00:00算起的毫秒数
        sign		    string       32	  是	验签参数。
        */

        //根据userId查询出对应的userInfo信息
        UserInfo userInfo = userInfoMapper.selectById(userId);


        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard", userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("email", userInfo.getEmail());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        //生成表单的工具类,需要传入表单发送请求的地址,还有要发送的数据,这里是要对接第三方平台,将我么你的数据传入进行存储到第三方数据库中
        String form = FormHelper.buildForm("http://localhost:9999/userBind/BindAgreeUserV2", paramMap);
        return form;
    }

    /**
     * 账户绑定的最后一阶段,将数据进行补充数据库即可
     * @param paraMap
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> paraMap) {


        //bindCode表示回调函数传递过来的绑定代码,我们要将这个传递回来的值进行数据库中的补充
        String bindCode = (String) paraMap.get("bindCode");
        //UserId
        String UserId = (String) paraMap.get("agentUserId");


        //根据第三方服务传递过来的UserId,查询出数据并且将bindCode进行写入
        //更新user_bind表(用户绑定表)
        LambdaQueryWrapper<UserBind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBind::getUserId,UserId);
        UserBind userBind = baseMapper.selectOne(wrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        //根据user_id修改user_bind表,因为数据一对一,所以可以根据user_id修改
        baseMapper.update(userBind,wrapper);



        //更新user_info表
        UserInfo userInfo = userInfoMapper.selectById(UserId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
