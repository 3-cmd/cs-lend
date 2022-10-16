package com.cs.core.service.impl;

import com.cs.core.pojo.entity.Dict;
import com.cs.core.mapper.DictMapper;
import com.cs.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

}
