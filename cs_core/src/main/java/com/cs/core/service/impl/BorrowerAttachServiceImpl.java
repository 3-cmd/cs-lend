package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.core.pojo.entity.BorrowerAttach;
import com.cs.core.mapper.BorrowerAttachMapper;
import com.cs.core.pojo.vo.BorrowerAttachVO;
import com.cs.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id) {
        LambdaQueryWrapper<BorrowerAttach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowerAttach::getBorrowerId, id);
        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(wrapper);
        return borrowerAttaches.stream().map(item->{
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            BeanUtils.copyProperties(item,borrowerAttachVO);
            return borrowerAttachVO;
        }).collect(Collectors.toList());
    }
}
