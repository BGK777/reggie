package com.BGK.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test() {
        double x = 2.0;
        int y = 4;
        x /= ++y;
        System.out.println(x);

    }
}
