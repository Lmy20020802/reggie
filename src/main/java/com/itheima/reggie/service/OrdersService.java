package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * @ClassName OrdersService
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 07:26
 **/
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);
}
