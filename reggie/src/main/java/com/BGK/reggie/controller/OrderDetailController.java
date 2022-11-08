package com.BGK.reggie.controller;

import com.BGK.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单明细管理
 */
@RestController
@RequestMapping("/orderDetail")
@Slf4j
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}
