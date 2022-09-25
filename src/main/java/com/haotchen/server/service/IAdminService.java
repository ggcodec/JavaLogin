package com.haotchen.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haotchen.server.pojo.Admin;
import com.haotchen.server.pojo.RespBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hexianwei
 * @since 2022-09-22
 */
public interface IAdminService extends IService<Admin> {


    /**
     * 登录之后返回token
     * @param username
     * @param password
     * @param request
     * @return
     */
    RespBean login(String username, String password, String code, HttpServletRequest request);

    Admin getAdminByUserName(String name);
}
