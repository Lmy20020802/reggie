package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName AddressBookMapper
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 09:22
 **/
@SuppressWarnings({"all"})
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}
