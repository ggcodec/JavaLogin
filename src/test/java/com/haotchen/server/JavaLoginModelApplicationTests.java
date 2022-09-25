package com.haotchen.server;

import com.haotchen.server.mapper.AdminMapper;
import com.haotchen.server.pojo.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class JavaLoginModelApplicationTests {

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void saveAdmin() {
        System.out.println(passwordEncoder.encode("123"));
    }

}
