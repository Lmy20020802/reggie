package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.sun.org.apache.bcel.internal.generic.LNEG;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @ClassName EmployeeController
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/22 09:28
 **/
@SuppressWarnings({"all"})
@Slf4j
@RestController
@RequestMapping("/employee")
@Api(tags = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登陆：
     * 参数一：登陆成功后，需要把Employee员工对象存入到session，表示登陆成功
     * 参数二：接收前端页面的登陆数据，post请求，请求数据为json类型的
     * 在登陆参数中添加一个@RequestBody注解,以根据请求的内容类型解析方法参数
     * 传的json中有一个key是username 还有一个是password,与Employee实体类中的属性命名必须一样
     * 否则无法封装成功
     * @param request
     * @param employee
     * @return
     */

    //http://localhost:8080/employee/login
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public R<Employee> login(@ApiParam("传入一个JSON字符串转换成Employee对象") @RequestBody Employee employee,
                             @ApiParam("request对象") HttpServletRequest request){

        //1.将页面提交的密码password进行md5进行处理
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登录失败结果
        if (emp==null){
            return R.error("登录失败");
        }

        //4.密码对比 如果不一致则返回
        if(!emp.getPassword().equals(password)){
            return R.error("密码失败");
        }

        //5.查看员工状态 如果为已禁用状态 则返回员工已禁用
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6.登录成功 将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    //http://localhost:8080/employee/logout

    /**
     * 员工退出
     * @param request
     * @return
     */

    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public R<String> logout(@ApiParam("request对象") HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //http://localhost:8080/employee
    @PostMapping
    public R<String> save(@ApiParam("传入一个JSON字符串转换成Employee对象") @RequestBody Employee employee,
                          @ApiParam("request对象") HttpServletRequest request) {
        log.info("新增员工,员工信息:{}",employee.toString());

        //设置初始密码123456 需要进行MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        Long empId=(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return  R.success("新增员工成功");
    }

    //http://localhost:80Z80/employee/page?page=1&pageSize=10
    @ApiOperation("员工信息分页查询带条件")
    @GetMapping("/page")
    public R<Page> page(@ApiParam("页码数") int page,
                        @ApiParam("页大小") int pageSize,
                        @ApiParam("模糊查询的条件") String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //分页构造器
        Page<Employee> pageInfo=new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();

        //添加过滤条件 使用like自带的判断条件
        //第一个参数:判断是否由内容 如果没有内容则不会执行like
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    //http://localhost:8080/employee
    @ApiOperation("修改员工信息")
    @PutMapping
    public R<String> upodate(@ApiParam("request对象") HttpServletRequest request,@ApiParam("传入一个JSON字符串转换成Employee对象")@RequestBody Employee employee){
        log.info(employee.toString());//查看employee是否封装上了
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
        //进行更新
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

//    http://localhost:8080/employee/1561912412804747266
    @ApiOperation("根据id查询员工信息")
    @GetMapping("/{id}")
    public R<Employee> getById(@ApiParam("映射url中的id占位符")@PathVariable Long id){
        log.info("根据id查询员工信息，获取到的id为{}",id);
        Employee employee = employeeService.getById(id);
        //如果查出来的employee是空 报错
        if (Objects.isNull(employee)){
            return R.error("没有查询到对应的一个信息");
        }
        return  R.success(employee);
    }
}
