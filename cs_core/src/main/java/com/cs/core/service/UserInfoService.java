package com.cs.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.entity.UserLoginRecord;
import com.cs.core.pojo.query.UserInfoQuery;
import com.cs.core.pojo.vo.LoginVO;
import com.cs.core.pojo.vo.RegisterVO;
import com.cs.core.pojo.vo.UserInfoVO;

import java.util.List;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO register);

    UserInfoVO login(LoginVO loginVO, String ip);

    IPage<UserInfo> listPage(Long page,Long limit, UserInfoQuery userInfoQuery);

    int updateLock(Long id, Integer status);

    List<UserLoginRecord> getLog(Long id);

    boolean checkMobile(String mobile);
}
