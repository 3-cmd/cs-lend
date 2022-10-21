package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.common.exception.Assert;
import com.cs.common.result.ResponseEnum;
import com.cs.common.utils.MD5;
import com.cs.core.mapper.UserAccountMapper;
import com.cs.core.mapper.UserLoginRecordMapper;
import com.cs.core.pojo.entity.UserAccount;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.pojo.entity.UserLoginRecord;
import com.cs.core.pojo.vo.LoginVO;
import com.cs.core.pojo.vo.RegisterVO;
import com.cs.core.pojo.vo.UserInfoVO;
import com.cs.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.serviceBase.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserLoginRecordMapper recordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO register) {
        Assert.notNull(register,ResponseEnum.SERVLET_ERROR);
        Assert.isPattern("\\w{6,24}",register.getPassword(),ResponseEnum.PASSWORD_NOT_PATTERN);
        Assert.isPattern("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",register.getEmail(),ResponseEnum.EMAIL_ERROR);
        UserInfo userInfo=new UserInfo();
        BeanUtils.copyProperties(register,userInfo);
        //1.首先判断用户是否已经注册
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getMobile,userInfo.getMobile());
        UserInfo user = this.getOne(wrapper);
        //断言查询出来的user为null,否则抛出异常  user为null表示此人没有进行注册
        Assert.isNull(user, ResponseEnum.MOBILE_EXIST_ERROR);
        //对用户的密码进行加密
        userInfo.setPassword(MD5.encrypt(userInfo.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.USER_AVATAR);
        userInfo.setNickName(userInfo.getMobile());
        //向User_info表中插入信息
        this.save(userInfo);
        //插入用户账户记录表user_account
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        //登录判断用户是否存在
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getMobile,loginVO.getMobile()).eq(UserInfo::getUserType,loginVO.getUserType());
        UserInfo user = this.getOne(wrapper);
        Assert.notNull(user,ResponseEnum.LOGIN_MOBILE_ERROR);
        //判断密码是否正确 将用户输入的密码进行加密,因为数据库中的密码是加密存储的
        Assert.equals(user.getPassword(),MD5.encrypt(loginVO.getPassword()),ResponseEnum.LOGIN_PASSWORD_ERROR);
        //判断用户是否被禁用
        Assert.equals(user.getStatus(),UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);
        //记录登录日志
        UserLoginRecord userLoginRecord=new UserLoginRecord();
        userLoginRecord.setUserId(user.getId());
        userLoginRecord.setIp(ip);
        recordMapper.insert(userLoginRecord);
        //生成token
        String token = JwtUtils.createToken(user.getId(), user.getName());
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        BeanUtils.copyProperties(user,userInfoVO);
        return userInfoVO;
    }

}
