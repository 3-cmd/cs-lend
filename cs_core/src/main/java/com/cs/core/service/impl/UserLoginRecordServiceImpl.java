package com.cs.core.service.impl;

import com.cs.core.pojo.entity.UserLoginRecord;
import com.cs.core.mapper.UserLoginRecordMapper;
import com.cs.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

}
