package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @ClassName OrderDto
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 12:50
 **/
@SuppressWarnings({"all"})
/**
 * @author LJM
 * @create 2022/5/3
 */
@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}