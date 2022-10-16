package com.cs.core.service.impl;

import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
