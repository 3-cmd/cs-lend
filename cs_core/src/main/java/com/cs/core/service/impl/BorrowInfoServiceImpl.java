package com.cs.core.service.impl;

import com.cs.core.pojo.entity.BorrowInfo;
import com.cs.core.mapper.BorrowInfoMapper;
import com.cs.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

}
