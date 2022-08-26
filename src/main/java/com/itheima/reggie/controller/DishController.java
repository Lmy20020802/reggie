package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSInput;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 新增菜品，由于前端传给我们的数据是json形式的，我们需要给这就接口传递参数，接收前端数据，
     * 但是我们发现，我们的Dish类中没有flavors属性，则不能自动一一对应接收数据，所以我们需要自定义一个类
     * 接收前端数据：DishDto
     * DTO:全称Data Transfer Object, 即数据传输对象，一般用于展示层与服务层之间的数据传输
     *      因为前端传给我们的数据和实体类中的属性不一致，所以需要使用DTO
     * @return
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public R<String> save(@ApiParam("传入一个JSON字符串转换成DishDto") @RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    @ApiOperation("菜品分页查询带条件")
    @GetMapping("/page")
    public R<Page> page(@ApiParam("页码数") int page,@ApiParam("页大小") int pageSize,@ApiParam("模糊查询的条件") String name){

        //构造一个分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();

        //添加过滤条件(模糊查询)
        dishLambdaQueryWrapper.like(name!=null,Dish::getName,name);

        //添加排序条件
        dishLambdaQueryWrapper.orderByAsc(Dish::getUpdateTime);

        //调用service方法进行查询
        dishService.page(pageInfo,dishLambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->
        {
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息 和 对应的口味信息
     * @param id
     * @return
     */
    @ApiOperation("根据id查询菜品信息 和 对应的口味信息")
    @GetMapping("/{id}")
    public R<DishDto>  getById(@ApiParam("传入的id")@PathVariable Long id){
        //扩展自定义查询方法
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public R<String> update(@ApiParam("传入一个JSON字符串转换成DishDto") @RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }


    @ApiOperation("删除菜品")
    @DeleteMapping
    public R<String> delete(@ApiParam("传入的id") @RequestParam List<Long> ids) {
        // 增加日志验证是否接收到前端参数。
        log.info("根据id删除一个菜品:{}", ids);
        //执行删除。
        dishService.deleteWithFlavor(ids);
        return R.success("删除菜品成功");
    }


    //http://localhost:8080/dish/status/0?ids=1562395264831053826
    /**
     * 根据id修改菜品的状态status(停售和起售)
     *
     *0停售，1起售。
     * @param status
     * @param
     * @return
     */
    @ApiOperation("根据id修改菜品的状态status(停售和起售)")
    @PostMapping("/status/{status}")
    public R<String> updateStatusById(@ApiParam("映射url中的status占位符") @PathVariable Integer status, @ApiParam("传入的id") Long[] ids) {
        // 增加日志验证是否接收到前端参数。
        log.info("根据id修改菜品的状态:{},id为：{}", status, ids);
        // 通过id查询数据库。修改id为ids数组中的数据的菜品状态status为前端页面提交的status。
        for (int i = 0; i < ids.length; i++) {
            Long id=ids[i];
            //根据id得到每个dish菜品。
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }

        return R.success("修改菜品状态成功");
    }

    @ApiOperation("显示当前菜品信息")
    @GetMapping("/list")
    public R<List<DishDto>> list(@ApiParam("Dish对象") Dish dish){
        List<DishDto> dishDtoList=null;
        if (dishDtoList!=null){
            //如果存在 直接返回 无需查询数据库
            return R.success(dishDtoList);
        }

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件 查询状态为1(起售状态)的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list=dishService.list(queryWrapper);

        dishDtoList=list.stream().map((item)->
        {
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //select * from dish_flavor where dish_id=?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());


        return  R.success(dishDtoList);
    }
}