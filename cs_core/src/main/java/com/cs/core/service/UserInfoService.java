package com.cs.core.service;

import com.cs.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.vo.LoginVO;
import com.cs.core.pojo.vo.RegisterVO;
import com.cs.core.pojo.vo.UserInfoVO;

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
}
