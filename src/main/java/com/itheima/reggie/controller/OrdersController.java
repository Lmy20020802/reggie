package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OrdersController
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 07:27
 **/
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/order")
@Slf4j
@Api(tags = "后台订单管理")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 后台查询订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @ApiOperation("后台查询订单明细")
    @GetMapping("/page")
    public R<Page> page(@ApiParam("页码数") int page,
                        @ApiParam("页大小") int pageSize,
                        @ApiParam("购买数量") String number,
                        @ApiParam("开始时间") String beginTime,
                        @ApiParam("结束时间") String endTime){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件  动态sql  字符串使用StringUtils.isnotEmpty这个方法来判断
        //这里使用了范围查询的动态SQL࿰c;这里是重点！！！
        queryWrapper.like(number!=null,Orders::getNumber,number)
                .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    //http://localhost:8080/order/submit
    @ApiOperation("下单")
    @PostMapping("/submit")
    public R<String> submit(@ApiParam("传入一个JSON字符串转换成Orders对象")@RequestBody Orders orders){
        log.info("订单数据:{}",orders);
        ordersService.submit(orders);
        return  R.success("下单成功");
    }

    @ApiOperation("修改订单状态")
    @PutMapping
    public R<String> orderstatusChange(@ApiParam("传入一个JSON字符串转换成Map集合")@RequestBody Map<String,String> map){
        String id = map.get("id");
        Long orderId = Long.parseLong(id);
        Integer status = Integer.parseInt(map.get("status"));
        if(orderId == null || status==null){
            return R.error("传入信息不合法");
        }
        Orders orders = ordersService.getById(orderId);
        orders.setStatus(status);
        ordersService.updateById(orders);
        return R.success("订单状态修改成功");
    }

    //抽离的一个方法࿰c;通过订单id查询订单明细࿰c;得到一个订单明细的集合
    //这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加࿰c;从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }
    /**
     * 用户端展示自己的订单分页查询
     * @param page
     * @param pageSize
     * @return
     * 遇到的坑：原来分页对象中的records集合存储的对象是分页泛型中的对象࿰c;里面有分页泛型对象的数据
     * 开始的时候我以为前端只传过来了分页数据࿰c;其他所有的数据都要从本地线程存储的用户id开始查询࿰c;
     * 结果就出现了一个用户id查询到 n个订单对象࿰c;然后又使用 n个订单对象又去查询 m 个订单明细对象࿰c;
     * 结果就出现了评论区老哥出现的bug(嵌套显示数据....)
     * 正确方法:直接从分页对象中获取订单id就行࿰c;问题大大简化了......
     */
    @ApiOperation("用户端展示自己的订单分页查询")
    @GetMapping("/userPage")
    public R<Page> page(@ApiParam("页码数") int page,@ApiParam("页大小") int pageSize){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> pageDto = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //这里是直接把当前用户分页的全部结果查询出来࿰c;要添加用户id作为查询条件࿰c;否则会出现用户可以查询到其他用户的订单情况
        //添加排序条件࿰c;根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,queryWrapper);
        //通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();
        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) ->{
            OrderDto orderDto = new OrderDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            List<OrderDetail> orderDetailList = this.getOrderDetailListByOrderId(orderId);
            BeanUtils.copyProperties(item,orderDto);
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());
        //使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }

    //客户端点击再来一单
    /**
     * 前端点击再来一单是直接跳转到购物车的࿰c;所以为了避免数据有问题࿰c;再跳转之前我们需要把购物车的数据给清除
     * ①通过orderId获取订单明细
     * ②把订单明细的数据的数据塞到购物车表中࿰c;不过在此之前要先把购物车表中的数据给清除(清除的是当前登录用户的购物车表中的数据)࿰c;
     * 不然就会导致再来一单的数据有问题；
     * (这样可能会影响用户体验࿰c;但是对于外卖来说࿰c;用户体验的影响不是很大࿰c;电商项目就不能这么干了)
     */
    @ApiOperation("客户端点击再来一单")
    @PostMapping("/again")
    public R<String> againSubmit(@ApiParam("传入一个JSON字符串转换成Map集合") @RequestBody Map<String,String> map){
        String ids = map.get("id");
        long id = Long.parseLong(ids);

        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        //获取该订单对应的所有的订单明细表
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        //通过用户id把原来的购物车给清空࿰c;这里的clean方法是视频中讲过的,建议抽取到service中,那么这里就可以直接调用了
//        shoppingCartService.clean();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            //把从order表中和order_details表中获取到的数据赋值给这个购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                //添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        //把携带数据的购物车批量插入购物车表  这个批量保存的方法要使用熟练！！！
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success("操作成功");
    }
}
