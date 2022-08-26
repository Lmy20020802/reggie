package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName MyMetaObjectHandler
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/23 15:37
 **/
@SuppressWarnings({"all"})

/**
 * 自定义元数据对象处理器
 */
@Component //加入到spring容器中
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 当前端提交过来例如保存请求的时候
     * 在执行sql前会来到这个类中，对字段进行填充
     * 参数：metaObject:元数据，封装了数据对象（employee对象）
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        //设置自动填充值
        //创建更新时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //创建更新人，因为我们需要从session中获取到当前登陆的用户，但是现在获取不到，我们先写死
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新字段自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        //在更新字段的时候我们就不需要再对创建字段进行修改了
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

}
