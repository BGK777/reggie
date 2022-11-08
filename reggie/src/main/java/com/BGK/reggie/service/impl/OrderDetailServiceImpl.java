package com.BGK.reggie.service.impl;

import com.BGK.reggie.mapper.OrderDetailMapper;
import com.BGK.reggie.pojo.OrderDetail;
import com.BGK.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}