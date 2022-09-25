package com.haotchen.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotchen.server.config.security.JwtTokenUtil;
import com.haotchen.server.mapper.AdminMapper;
import com.haotchen.server.pojo.Admin;
import com.haotchen.server.pojo.RespBean;
import com.haotchen.server.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hexianwei
 * @since 2022-09-22
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Autowired
    private AdminMapper adminMapper;



    @Override
    public RespBean login(String username, String password, String code, HttpServletRequest request) {
        /*
            时间: 2022/9/24-13:09
            注释: 校验用户传入信息
        */
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (Objects.isNull(userDetails) || !passwordEncoder.matches(password,userDetails.getPassword())) {
            return RespBean.fail("用户名或密码不正确!");
        }
        if (!userDetails.isEnabled()) {
            return RespBean.fail("账号被禁用,请联系管理员!");
        }
        if (Objects.isNull(code) || !request.getSession().getAttribute("kaptchaVerifyCode").equals(code)) {
            return RespBean.fail("验证码错误,请刷新后重试!");
        }

        /*
            时间: 2022/9/24-13:08
            注释: 更新用户登录信息
        */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        /*
            时间: 2022/9/24-13:08
            注释: 生成token
        */
        String token = jwtTokenUtil.getToken(userDetails);
        HashMap<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead",JwtTokenUtil.jwtTokenHead);

        /*
            时间: 2022/9/24-13:08
            注释: 返回成功结果
        */
        return RespBean.success("登录成功!",tokenMap);
    }

    @Override
    public Admin getAdminByUserName(String name) {

        return adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, name)
                .eq(Admin::isEnabled, true));
    }
}
