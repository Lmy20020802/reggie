package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时添加菜品对应的口味数据(多表操作:dish,dish_flavor)
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void deleteWithFlavor(List<Long> ids);
}

