package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName OrdersMapper
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 07:26
 **/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
