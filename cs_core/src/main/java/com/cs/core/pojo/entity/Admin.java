package com.cs.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("admin")
@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
}
