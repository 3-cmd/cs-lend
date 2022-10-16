package com.cs.core.service.impl;

import com.cs.core.pojo.entity.UserAccount;
import com.cs.core.mapper.UserAccountMapper;
import com.cs.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}
