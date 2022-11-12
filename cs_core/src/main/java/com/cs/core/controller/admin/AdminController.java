//package com.cs.core.controller.admin;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.cs.common.result.R;
//import com.cs.core.mapper.AdminMapper;
//import com.cs.core.pojo.entity.Admin;
//import com.cs.core.service.AdminService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.UUID;
//
//
//@RestController
//@RequestMapping("/admin/admin")
//public class AdminController {
//    @Autowired
//    private AdminService adminService;
//
//    @PostMapping("/login")
//    public R longin(@RequestBody Admin admin) {
//        return R.success().add("token","admin");
//    }
//
//    @GetMapping("/info")
//    public R getInfo() {
//        return R.success()
//                .add("role","[admin]")
//                .add("name","admin")
//                .add("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
//    }
//
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
