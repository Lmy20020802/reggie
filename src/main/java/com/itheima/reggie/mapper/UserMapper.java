package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName UserMapper
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 08:16
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
