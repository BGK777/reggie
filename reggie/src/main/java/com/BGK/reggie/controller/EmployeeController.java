package com.BGK.reggie.controller;

import com.BGK.reggie.common.BaseContext;
import com.BGK.reggie.common.R;
import com.BGK.reggie.pojo.Employee;
import com.BGK.reggie.service.EmployeeService;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工管理
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将页面体检的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username进行数据库查询
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        //3.如果没有查询到，返回登录失败
        if (emp == null) {
            return R.error("用户不存在！");
        }

        //4.密码对比，如果不一致返回登录失败
        if (!password.equals(emp.getPassword())) {
            return R.error("密码错误！");
        }

        //5.查看员工状态，如果禁用则返回失败
        if (emp.getStatus() == 0) {
            return R.error("该账号已被禁用！");
        }

        //登录成功，将员工id存入session并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前员工信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee) {
        //设置初始默认密码,并使用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户id,并设置修改人
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //新增员工
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize,String name){

        //添加分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        //添加条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);

        //执行查询并返回
        employeeService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    /**
     * 修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        Long id = Thread.currentThread().getId();
        log.info("线程id===={}", BaseContext.getCurrentId());

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);
        return R.success("修改员工信息成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<Employee> getOneById(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
