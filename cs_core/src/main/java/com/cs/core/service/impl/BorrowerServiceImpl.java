package com.cs.core.service.impl;

import com.cs.common.exception.BusinessException;
import com.cs.core.enums.BorrowerStatusEnum;
import com.cs.core.mapper.BorrowerAttachMapper;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.pojo.entity.Borrower;
import com.cs.core.mapper.BorrowerMapper;
import com.cs.core.pojo.entity.BorrowerAttach;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.pojo.vo.BorrowerVO;
import com.cs.core.service.BorrowerAttachService;
import com.cs.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowersByUserId(BorrowerVO borrowerVO, Long userId) {
        //判断用户是否已经绑定账号,如果没有绑定,则提示用户去绑定
        //根据user_id查询出borrower需要的相关信息
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer bindStatus = userInfo.getBindStatus();
        if (!bindStatus.equals(1)) throw new BusinessException("用户未绑定账号,请先去绑定账户");

        Borrower borrower = new Borrower();
        borrower.setUserId(userId);
        BeanUtils.copyProperties(borrowerVO,borrower);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        //当提交表单后,设置认证状态为认证中
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);
        //设置借款人资源表borrower_attach,用来存放借款人的上传的照片的路径
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(item->{
            item.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(item);
        });
        //修改userinfo中借款人的状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
