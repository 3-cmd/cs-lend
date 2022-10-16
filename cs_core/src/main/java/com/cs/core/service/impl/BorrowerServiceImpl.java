package com.cs.core.service.impl;

import com.cs.core.pojo.entity.Borrower;
import com.cs.core.mapper.BorrowerMapper;
import com.cs.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

}
