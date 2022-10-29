package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.common.exception.Assert;
import com.cs.common.result.ResponseEnum;
import com.cs.common.utils.MD5;
import com.cs.core.mapper.UserAccountMapper;
import com.cs.core.mapper.UserLoginRecordMapper;
import com.cs.core.pojo.entity.UserAccount;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.pojo.entity.UserLoginRecord;
import com.cs.core.pojo.query.UserInfoQuery;
import com.cs.core.pojo.vo.LoginVO;
import com.cs.core.pojo.vo.RegisterVO;
import com.cs.core.pojo.vo.UserInfoVO;
import com.cs.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.serviceBase.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO register) {
        Assert.notNull(register, ResponseEnum.SERVLET_ERROR);
        Assert.isPattern("\\w{6,24}", register.getPassword(), ResponseEnum.PASSWORD_NOT_PATTERN);
        Assert.isPattern("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", register.getEmail(), ResponseEnum.EMAIL_ERROR);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(register, userInfo);
        //1.首先判断用户是否已经注册
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getMobile, userInfo.getMobile());
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
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getMobile, loginVO.getMobile()).eq(UserInfo::getUserType, loginVO.getUserType());
        UserInfo user = this.getOne(wrapper);
        Assert.notNull(user, ResponseEnum.LOGIN_MOBILE_ERROR);
        //判断密码是否正确 将用户输入的密码进行加密,因为数据库中的密码是加密存储的
        Assert.equals(user.getPassword(), MD5.encrypt(loginVO.getPassword()), ResponseEnum.LOGIN_PASSWORD_ERROR);
        //判断用户是否被禁用
        Assert.equals(user.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);
        //记录登录日志,因为我们有张表就是专门做登录日志的,所以每次进行登录时就应该将登陆日志的表中添加信息
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(user.getId());
        userLoginRecord.setIp(ip);
        recordMapper.insert(userLoginRecord);
        //生成token
        String token = JwtUtils.createToken(user.getId(), user.getName());
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    /**
     * 分页条件查询
     *
     * @param page          当前页码
     * @param limit         每页显示条目数
     * @param userInfoQuery 条件的封装
     * @return 返回分页的分页对象
     */
    @Override
    public IPage<UserInfo> listPage(Long page, Long limit, UserInfoQuery userInfoQuery) {
        IPage<UserInfo> iPage=new Page<>(page,limit);
        //为分页添加条件查询
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.like(userInfoQuery.getUserType()!=null,UserInfo::getUserType,userInfoQuery.getUserType())
                .like(userInfoQuery.getStatus()!=null , UserInfo::getStatus,userInfoQuery.getStatus())
                .like(!StringUtils.isBlank(userInfoQuery.getMobile()),UserInfo::getMobile,userInfoQuery.getMobile());
        IPage<UserInfo> ipage = this.page(iPage, wrapper);
        return  ipage;
    }

    /**
     * 根据id修改所得状态
     * @param id 要修改的数据的id
     * @param status 要修改成的状态 我们要将状态修改为status
     * @return 返回影响数据库的行数
     */
    @Override
    public int updateLock(Long id, Integer status) {
        UserInfo userInfo=new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        return userInfoMapper.updateById(userInfo);
    }

    /**
     * 根据用户的id,查询出用户所在的登录记录表中的信息
     * @param userId 用户的id
     * @return 查询到的登录日志的数据
     */
    @Override
    public List<UserLoginRecord> getLog(Long userId) {
        //根据用户的id,查询出用户所在的登录记录表中的信息
        LambdaQueryWrapper<UserLoginRecord> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,UserLoginRecord::getUserId,userId);
        return recordMapper.selectList(wrapper);
    }

    /**
     * 判断手机号是否已经注册,在发送验证码之前判断
     * @param mobile 手机号
     * @return 返回true代表已经注册 false表示未被注册
     */
    @Override
    public boolean checkMobile(String mobile) {
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(mobile!=null, UserInfo::getMobile,mobile);
        //如果查询不到,则证明该手机号没有被注册
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);
        return userInfo==null;


    }

}
