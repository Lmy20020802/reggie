package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName LoginCheckFilter
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/22 10:57
 **/
@SuppressWarnings({"all"})
/**
 * 自定义过滤器：
 * 检查用户是否已经完成登陆
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3. 如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            //后面代码不需要执行了，直接return让方法结束
            return;
        }
        //4-1 判断登陆状态，如果已登录，则直接放行
        //从session中获取登陆存储的信息
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登陆,用户的id为:{}",request.getSession().getAttribute("employee"));
            Long empId=(Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //4-2 判断登陆状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登陆,用户的id为:{}",request.getSession().getAttribute("user"));
            Long userId=(Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //5. 如果未登录则返回未登录结果，通过输出流的方式，向客户端页面响应数据
        log.info("用户未登陆");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}