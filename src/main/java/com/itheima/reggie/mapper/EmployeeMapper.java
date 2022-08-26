package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName EmployeeMapper
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/22 09:23
 **/
@SuppressWarnings({"all"})
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
    
}
