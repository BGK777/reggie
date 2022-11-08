package com.BGK.reggie.Filter;

import com.BGK.reggie.common.BaseContext;
import com.BGK.reggie.common.R;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.MARSHAL;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次的url
        String requestURL = request.getRequestURI();

        log.info("拦截到请求：{}",request.getRequestURI());

        //设置不需要拦截的url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(requestURL, urls);

        //3.不需要处理，直接放行
        if(check){
            log.info("本次请求{}不需要处理",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断后台管理登录状态，如果已登录，直接放行
        Long empId = (Long) request.getSession().getAttribute("employee");
        if( empId != null){
            log.info("用户已登录，id为：{}",empId);

            //保存当前用户id到基于ThreadLocal的工具包里
            BaseContext.setCurrentId(empId);

            Long id = Thread.currentThread().getId();
            log.info("线程id===={}",BaseContext.getCurrentId());

            filterChain.doFilter(request,response);
            return;
        }

        //4-2.判断移动端登录状态，如果已登录，直接放行
        Long userId = (Long) request.getSession().getAttribute("user");
        if( userId != null){
            log.info("用户已登录，id为：{}",userId);

            //保存当前用户id到基于ThreadLocal的工具包里
            BaseContext.setCurrentId(userId);

            Long id = Thread.currentThread().getId();
            log.info("线程id===={}",BaseContext.getCurrentId());

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录，则返回未登录结果,通过输出流向客户端返回相应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String requestURL,String[] urls){
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if(match){
                return true;
            }
        }
        return false;
    }
}
