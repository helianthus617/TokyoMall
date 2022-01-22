package com.atguigu.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.bouncycastle.crypto.generators.BCrypt;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GulimallMemberApplicationTests {
    @Test
    public void md5contextLoads() {
//        原始md5 可以由彩虹表破解      e10adc3949ba59abbe56e057f20f883e
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
    }

    @Test
    public void md5contextLoadsssalt() {
//        原始md5 + 盐$1$
//        $1$salt$638tR8bROOvPnPklDQ9Vf/
//        $1$salts$irOKHaQMfmNz5HmC9lqfz.
        String s = Md5Crypt.md5Crypt("123456".getBytes(), "$1$salts");
        System.out.println(s);
    }

    @Test
    public void spring() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        //$2a$10$xYWvaa2ryZHzXDyRJTEAZuSqe7P2xLBTF5iNZ8SJuM53B/aVoC5LW
        //$2a$10$ZiADT/o7quKCOZBHIDdavehWr/fYbl8LA50a/rd/IivQYrjyZL9pO
        //$2a$10$Q7IK/qEmRgGilrkyzSAiuuYafFx6yXEvlgWh9tg1nwZEeho22K8SK
        boolean matches = passwordEncoder.matches("123456", "$2a$10$ZiADT/o7quKCOZBHIDdavehWr/fYbl8LA50a/rd/IivQYrjyZL9pO");
        boolean matches1 = passwordEncoder.matches("123456", "$2a$10$Q7IK/qEmRgGilrkyzSAiuuYafFx6yXEvlgWh9tg1nwZEeho22K8SK");

        System.out.println(matches + "    " + matches1);
    }
}
