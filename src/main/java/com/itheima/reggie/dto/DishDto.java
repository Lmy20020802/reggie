package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类继承了Dish，但是我们知道类中的属性都是私有的
 * 子类虽然可以继承父类的私有属性，但是不能直接访问私有属性，除非父类开放了私有属性的访问接口
 * 因为@Data注解自动生成了getter方法，所以可以通过getter和setter访问私有属性
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

