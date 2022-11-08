package com.BGK.reggie.service.impl;

import com.BGK.reggie.mapper.EmployeeMapper;
import com.BGK.reggie.pojo.Employee;
import com.BGK.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
