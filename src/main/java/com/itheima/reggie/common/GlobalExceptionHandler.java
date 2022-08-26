package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @ClassName GlobalExceptionHandler
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/22 14:37
 **/
@SuppressWarnings({"all"})
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法：
     * 一旦controller抛出这种SQLIntegrityConstraintViolationException异常
     * 就会被这个方法处理
     * @return
     */

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        //首先判断异常信息中是否有Duplicate entry关键字信息，因为出现异常不一定都是双重输入重复异常
        if (exception.getMessage().contains("Duplicate entry")){
            //这个时候肯定是用户名重复了
            //我们提取出重复的用户名，使用空格分割
            String[] split = exception.getMessage().split(" ");
            String msg = split[2] + "已存在";
            //返回错误信息
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 异常处理方法：自定义异常，删除分类判断是否有菜品或者套餐
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
