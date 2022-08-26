package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/category")
@Slf4j
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("新增分类")
    @PostMapping
    public R<String> save(@ApiParam("传入一个JSON字符串转换成Category对象") @RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    //http://localhost:8080/category/page?page=1&pageSize=10
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public R<Page> page(@ApiParam("页码数") int page, @ApiParam("页大小") int pageSize){
        //分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //条件过滤器(排序)
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    @DeleteMapping
    @ApiOperation("删除分类")
    public R<String> deleteById(@ApiParam("传入的id") Long ids){
        log.info("删除的id为:{}",ids);

        categoryService.remove(ids);
        return R.success("删除成功!");
    }

    @PutMapping
    @ApiOperation("修改分类")
    public R<String> updateById(@ApiParam("传入一个JSON字符串转换成Category对象")@RequestBody Category category){
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功!");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @ApiOperation("根据条件查询分类数据")
    @GetMapping("/list")
    public R<List<Category>> list(@ApiParam("传入一个Category对象") Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(category.getType()!=null, Category::getType,category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}