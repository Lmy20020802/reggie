package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐 同时需要保存套餐的菜品的关联关系
     *
     * @param setmealDto
     */

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息 操作setmeal 执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息 操作setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {

        //select count(*) from setmeal where id in(1,2,3) and status=1
        //查找套餐状态 确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除 则抛出异常
            throw new CustomException("套餐为起售状态,无法删除");
        }

        //如果可以删除 先删除套餐中的数据 -- setmeal
        this.removeByIds(ids);

        //删除关系表中的数据 -- setmeal_dish
        //delete from setmeal_dish where setmeal_id in(1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 通过id查询套餐信息， 同时还要查询关联表setmeal_dish的菜品信息进行回显。
     *
     * @param id 待查询的id
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 根据id查询setmeal表中的基本信息
        Setmeal setmeal=this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        // 对象拷贝。
        BeanUtils.copyProperties(setmeal,setmealDto);
        // 查询关联表setmeal_dish的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        //设置套餐菜品属性
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 更新套餐信息，不仅要更新setmeal基本信息， 还要更新套餐所对应的菜品到setmeal_dish表
     *
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 保存setmeal表中的基本数据。
        this.updateById(setmealDto);
        // 先删除原来的套餐所对应的菜品数据。
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 更新套餐关联菜品信息。setmeal_dish表。
        // Field 'setmeal_id' doesn't have a default value] with root cause
        // 所以需要处理setmeal_id字段。
        // 先获得套餐所对应的菜品集合。
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //每一个item为SetmealDish对象。
        setmealDishes=setmealDishes.stream().map((item)->
        {
            //设置setmeal_id字段
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 重新保存套餐对应菜品数据
        setmealDishService.saveBatch(setmealDishes);
    }
}