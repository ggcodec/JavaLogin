package com.haotchen.server.controller;

import com.haotchen.server.pojo.Admin;
import com.haotchen.server.pojo.AdminLoginParam;
import com.haotchen.server.pojo.RespBean;
import com.haotchen.server.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Objects;


/**
 * 登录
 */
@RestController
@Api("LoginController")
public class LoginController {

    @Autowired
    @Qualifier("adminServiceImpl")
    IAdminService service;

    @ApiOperation(value = "登录之后返回token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLoginParam loginBean, HttpServletRequest request) {
        return service.login(loginBean.getUsername(), loginBean.getPassword(),loginBean.getCode(), request);
    }



    @ApiOperation(value = "退出当前登录账号")
    @PostMapping("/logout")
    public RespBean logout() {
        return RespBean.success("注销成功!");
    }



    @ApiOperation(value = "获取当前用户登录信息")
    @PostMapping("/admin/info")
    public Admin getAdminInfo(Principal principal) {
        if (Objects.isNull(principal)) {
            return null;
        }

        String name = principal.getName();
        Admin admin = service.getAdminByUserName(name);
        if (admin == null) {
            return null;
        }
        admin.setPassword(null);
        return admin;
    }


}
