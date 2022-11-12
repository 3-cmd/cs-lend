package com.cs.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cs.common.exception.Assert;
import com.cs.common.exception.BusinessException;
import com.cs.common.result.ResponseEnum;
import com.cs.core.enums.BorrowerStatusEnum;
import com.cs.core.enums.IntegralEnum;
import com.cs.core.mapper.BorrowerAttachMapper;
import com.cs.core.mapper.UserInfoMapper;
import com.cs.core.mapper.UserIntegralMapper;
import com.cs.core.pojo.entity.Borrower;
import com.cs.core.mapper.BorrowerMapper;
import com.cs.core.pojo.entity.BorrowerAttach;
import com.cs.core.pojo.entity.UserInfo;
import com.cs.core.pojo.entity.UserIntegral;
import com.cs.core.pojo.vo.BorrowerApprovalVO;
import com.cs.core.pojo.vo.BorrowerAttachVO;
import com.cs.core.pojo.vo.BorrowerDetailVO;
import com.cs.core.pojo.vo.BorrowerVO;
import com.cs.core.service.BorrowerAttachService;
import com.cs.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.core.service.DictService;
import com.cs.serviceBase.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
    @Resource
    private DictService dictService;
    @Resource
    private BorrowerAttachService borrowerAttachService;
    @Resource
    private UserIntegralMapper userIntegralMapper;
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

    @Override
    public Integer getBorrowerStatus(HttpServletRequest request) {
        String token = request.getHeader("token");
        //断言不为null,如果为null则抛出异常
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);
        Long userId = JwtUtils.getUserId(token);
        //一个user对应一个借款人,所以只能查询查询出来一个
        LambdaQueryWrapper<Borrower> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Borrower::getUserId,userId);
        Borrower one = baseMapper.selectOne(wrapper);
        //如果查询不出来borrower证明该用户没有认证,那么直接返回未认证
        if (one == null) return BorrowerStatusEnum.NO_AUTH.getStatus();
        Integer status = one.getStatus();
        return status;
    }

    /**
     * 2022-11-07
     * 管理员对借款账户的审批时,需要先查询到借款人的全部信息
     * @param currentPage 当前页码
     * @param limit 一页展示的数据
     * @param keyword 模糊匹配的关键字,根据用户姓名,手机,身份证进行匹配查询
     * @return 返回全部分页page对象
     */
    @Override
    public IPage<Borrower> getBorrower(int currentPage, int limit, String keyword) {
        LambdaQueryWrapper<Borrower> wrapper=new LambdaQueryWrapper<>();
        IPage<Borrower> page=new Page<>();
        page.setCurrent(currentPage);
        page.setSize(limit);
        wrapper.like(keyword!=null , Borrower::getName,keyword)
                .or().like(keyword!=null,Borrower::getMobile,keyword)
                .or().like(keyword!=null, Borrower::getIdCard,keyword);
        IPage<Borrower> borrowerIPage = baseMapper.selectPage(page, wrapper);
        return borrowerIPage;

    }

    @Override
    public BorrowerDetailVO showDetails(Long id) {
        LambdaQueryWrapper<Borrower> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(id!=null,Borrower::getId,id);
        Borrower borrower = baseMapper.selectOne(wrapper);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        //拷贝基本信息
        BeanUtils.copyProperties(borrower,borrowerDetailVO);
        //由于我们存储在borrower表中的信息为dict中的值,我们要根据这些值来查询出相关的文本信息给到前端
        //性别
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");
        //婚否
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");
        //从dict表中进行查询然后赋值 ,根据dict_code和value两个条件来取值

        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());
        //设置下拉列表选中内容
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);

        //获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList =  borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    @Override
    public void approvalBorrowerAppeal(BorrowerApprovalVO approvalVO) {
        //获取借款额度申请id
        Long borrowerId = approvalVO.getBorrowerId();
        //获取借款额度申请对象
        Borrower borrower = baseMapper.selectById(borrowerId);
        //设置借款人表中的申请状态
        borrower.setStatus(approvalVO.getStatus());
        baseMapper.updateById(borrower);
        //选择 如果不通过,那么没有积分
        if (BorrowerStatusEnum.getMsgByStatus(approvalVO.getStatus()).equals("认证失败")) return;
        //如果通过,计算积分信息表 user_integral  这个表中分别存储了根据不同的参数来计算积分放入表中
        //获取用户id
        Long userId = borrower.getUserId();
        //获取用户对象
        UserInfo userInfo = userInfoMapper.selectById(userId);
        //获取用户原始积分
        Integer totalIntegral=userInfo.getIntegral();
        //例如:根据基本信息评定积分
        UserIntegral userIntegral=new UserIntegral();
        userIntegral.setUserId(userId);
        Integer infoIntegral = approvalVO.getInfoIntegral();
        if (infoIntegral>=100) infoIntegral=100;
        if (infoIntegral<=30) infoIntegral=30;
        userIntegral.setIntegral(infoIntegral);
        userIntegral.setContent("根据基本信息来评定积分");
        userIntegralMapper.insert(userIntegral);
        totalIntegral+=infoIntegral;
        //根据房产信息评定积分
        if (approvalVO.getIsHouseOk()){
            userIntegral=new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            totalIntegral+=IntegralEnum.BORROWER_HOUSE.getIntegral();
        }
        //根据身份证信息评定积分
        if (approvalVO.getIsIdCardOk()){
            userIntegral=new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            totalIntegral+=IntegralEnum.BORROWER_IDCARD.getIntegral();
        }
        //根据车辆信息评定积分
        if (approvalVO.getIsCarOk()){
            userIntegral=new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            totalIntegral+=IntegralEnum.BORROWER_CAR.getIntegral();
        }
        //取出借款人对应的用户的积分,然后在加上新添加的积分,计算出总积分,然后将此总积分填入到user_info表中
        userInfo.setIntegral(totalIntegral);
        //修改借款人认证的状态
        userInfo.setBorrowAuthStatus(approvalVO.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
