package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    //身份证号码，不过在数据库中的名称是id_number，
    //但是我们在application.yml配置文件中开启了按照驼峰命名法映射，
    //所以以后在配置yml的时候一定记得要开启这个配置
    private String idNumber; //身份证号码

    private Integer status;

    @TableField(fill = FieldFill.INSERT)  //插入时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)  //插入时填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //插入和更新时填充字段
    private Long updateUser;

}
