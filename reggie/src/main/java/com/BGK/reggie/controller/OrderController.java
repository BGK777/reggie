package com.BGK.reggie.controller;

import com.BGK.reggie.common.R;
import com.BGK.reggie.pojo.Orders;
import com.BGK.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单管理
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("order=====>{}",orders);
       orderService.submit(orders);
        return R.success("下单成功^_^");
    }
}
