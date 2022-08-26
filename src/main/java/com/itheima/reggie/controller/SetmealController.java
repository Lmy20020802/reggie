package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SetmealController
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/24 19:46
 **/
@SuppressWarnings({"all"})

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐管理")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;


    //http://localhost:8080/setmeal
    @ApiOperation("新增套餐")
    @PostMapping
    private R<String> save(@ApiParam("传入一个JSON字符串转换成SetmealDto对象")@RequestBody  SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return  R.success("新增套餐成功");
    }

    //http://localhost:8080/setmeal/page?page=1&pageSize=10
    @ApiOperation("分页查询带条件")
    @GetMapping("/page")
    public R<Page> page(@ApiParam("页码数")int page,
                        @ApiParam("页大小")int pageSize,
                        @ApiParam("模糊查询的条件") String name){

        //分页构造器对象
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        //添加查询条件 根据name进行like模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);

        //添加排序条件 根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //调用service方法进行查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list=records.stream().map((item)->
        {
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String setmealName = category.getName();
                setmealDto.setCategoryName(setmealName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    //http://localhost:8080/setmeal?ids=1562425716992155649
    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @ApiOperation("删除套餐")
    @DeleteMapping
    public R<String> delete(@ApiParam("传入的id") @RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.deleteWithDish(ids);
        return  R.success("套餐数据删除成功");
    }

    //http://localhost:8080/setmeal/status/0?ids=1562425716992155649
    /**
     * 根据id修改菜品的状态status(停售和起售)
     *
     * 0停售，1起售。
     * @param status
     * @param
     * @return
     */
    @ApiOperation("根据id修改菜品的状态status(停售和起售)")
    @PostMapping("/status/{status}")
    public R<String> updateStatusById(@ApiParam("映射url的status占位符") @PathVariable Integer status,
                                      @ApiParam("传入的id") Long[] ids) {
        // 增加日志验证是否接收到前端参数。
        log.info("根据id修改菜品的状态:{},id为：{}", status, ids);
        // 通过id查询数据库。修改id为ids数组中的数据的菜品状态status为前端页面提交的status。
        for (int i = 0; i < ids.length; i++) {
            Long id=ids[i];
            //根据id得到每个dish菜品。
            Setmeal setmeal=setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改菜品状态成功");
    }


    //http://localhost:8080/setmeal/1562425716992155649
    /**
     * 根据id查询套餐信息
     *(套餐信息的回显)
     * @param id
     * @return
     */
    @ApiOperation("根据id查询套餐信息")
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@ApiParam("映射url的id占位符") @PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询。、
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    //http://localhost:8080/setmeal
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("修改套餐成功")
    @PutMapping
    public R<String> update(@ApiParam("传入一个JSON字符串转换成SetmealDto对象") @RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @ApiOperation("根据条件查询套餐数据")
    @GetMapping("/list")
    public R<List<Setmeal>> list(@ApiParam("传入的Setmeal对象") Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @ApiOperation("根据菜品id查询遍历信息")
    @GetMapping("/dish/{id}")
    public R<List<Dish>> dish(@ApiParam("映射url的id占位符") @PathVariable("id") Long SetmealId){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,SetmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        LambdaQueryWrapper<Dish> queryWrapper2 = new LambdaQueryWrapper<>();
        ArrayList<Long> dishIdList = new ArrayList<>();
        for (SetmealDish setmealDish : list) {
            Long dishId = setmealDish.getDishId();
            dishIdList.add(dishId);
        }
        queryWrapper2.in(Dish::getId, dishIdList);
        List<Dish> dishList = dishService.list(queryWrapper2);
        return R.success(dishList);
    }
}
