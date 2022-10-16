package com.cs.core.service.impl;

import com.cs.core.pojo.entity.LendItem;
import com.cs.core.mapper.LendItemMapper;
import com.cs.core.service.LendItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

}
