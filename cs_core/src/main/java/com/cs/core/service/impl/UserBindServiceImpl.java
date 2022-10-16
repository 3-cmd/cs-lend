package com.cs.core.service.impl;

import com.cs.core.pojo.entity.UserBind;
import com.cs.core.mapper.UserBindMapper;
import com.cs.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
