package com.cs.core.service;

import com.cs.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author cs
 * @since 2022-10-13
 */
public interface UserBindService extends IService<UserBind> {

    String commitBind(UserBindVO userBindVO, Long userId);

    void notify(Map<String, Object> paraMap);

    String getBindCodeByUserId(Long userId);
}
