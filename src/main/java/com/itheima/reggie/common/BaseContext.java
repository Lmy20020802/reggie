package com.itheima.reggie.common;

import com.sun.org.apache.bcel.internal.generic.RET;

/**
 * @ClassName BaseContext
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/23 16:06
 **/
@SuppressWarnings({"all"})

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登陆用户的id
 * 以线程为作用域，不用担心保存的副本弄混淆了（具有隔离性）
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置当前的id，拷贝到当前线程的副本中，方便以后取出
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
