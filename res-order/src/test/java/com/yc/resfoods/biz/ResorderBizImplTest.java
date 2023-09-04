package com.yc.resfoods.biz;

import com.yc.resfoods.OrderApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrderApp.class})
public class ResorderBizImplTest {
    @Autowired
    private ResorderBiz resorderBiz;

    @Test
    public void order() {
//        resorderBiz.
    }
}