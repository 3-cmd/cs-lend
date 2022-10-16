package com.cs.core.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.common.result.R;
import com.cs.core.mapper.AdminMapper;
import com.cs.core.pojo.entity.Admin;
import com.cs.core.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

}
