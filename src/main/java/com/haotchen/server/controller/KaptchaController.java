package com.haotchen.server.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@Api
public class KaptchaController {
    @Resource
    private Producer kaptchaProducer;

    @GetMapping("/vc")
    @ApiOperation(value = "创建验证码",produces = "image/jpeg")
    public void createVerifyCode(HttpServletRequest request, HttpServletResponse response)  {
        // >>> 注释: 响应立即过期  <<<
        response.setDateHeader("Expires", 0);

        // >>> 注释: 不缓存任何图片数据 <<<
        response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate");
        response.setHeader("Cache-Control", "post-check=0,pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/png");

        // >>> 注释: 生成验证码字符文本 <<<
        String verifyCode = kaptchaProducer.createText();

        // >>> 注释: 生成的验证码文本存放到session中 <<<
        request.getSession().setAttribute("kaptchaVerifyCode", verifyCode);

        // >>> 注释: 创建验证码对应的图片,并响应给前端 <<<
        BufferedImage image = kaptchaProducer.createImage(verifyCode);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // >>> 注释: 输出和关闭输出 <<<
            try {
                out.flush();
                out.close();
            }catch (Exception e) {
                throw new RuntimeException();
            }
        }

    }
}