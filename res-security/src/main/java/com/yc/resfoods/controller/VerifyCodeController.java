package com.yc.resfoods.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@RestController
@Slf4j
@RequestMapping("resfood") // http://localhost:port/resfood
@Api(tags = "验证码获取")
public class VerifyCodeController {

    // http://localhost:port/resfood/code.action
    @GetMapping("/code.action")
    public HttpEntity image(HttpSession session) throws IOException {
        // 1 创建对象，在内存中存图片（验证码图片对象）
        int width = 100;
        int height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        // 2 美化图片
        // 2.1 填充背景色
        Graphics graphics = image.getGraphics(); // 画笔对象
        graphics.setColor(Color.PINK); // 画笔的颜色
        graphics.fillRect(0, 0, width, height); // fillRect方法可以画背景色
        // 2.2 画边框
        graphics.setColor(Color.red);
        graphics.drawRect(0, 0, width-1, height-1); // drawRect方法可以画出边框
        // 2.3 TODO: 验证范围更大，写验证码
        String str = "abcdefj12345";
        Random random = new Random(); // 创建随机对象
        StringBuffer sb = new StringBuffer();

        for (int i=1; i<=4; i++){ // 循环4次，产生4位的验证码
            int index = random.nextInt(str.length()); // 生成角标
            // 随机字符
            char ch = str.charAt(index);
            sb.append(ch);
            // TODO: 每个字的颜色不同，位置不同。
            // 绘制随机字符到 graphics
            graphics.setColor(Color.BLACK); // 画笔的颜色
            graphics.drawString(ch+"" , width/5*i, height/2); // 验证码
        }
        session.setAttribute("code", sb.toString());

        // 随机数：
        // 2.4 画干扰线 （TODO：升级 —— 曲线，每根线的颜色不一样）
        graphics.setColor(Color.GREEN);
        for (int i=0; i<5; i++){
            int x1 = random.nextInt(width);
            int x2 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int y2 = random.nextInt(height);
            graphics.drawLine(x1, x2, y1, y2);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "png", byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        InputStreamResource inputStreamResource = new InputStreamResource(byteArrayInputStream);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Pragma", "No-cache");
        httpHeaders.set("Cache-Control", "No-cache");
        return (HttpEntity) ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .contentType(MediaType.IMAGE_PNG)
                .body(inputStreamResource);
    }
}
