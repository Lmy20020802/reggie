package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @ClassName WebMvcConfig
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/22 08:48
 **/
@SuppressWarnings({"all"})
/**
 * mvc静态资源映射配置类
 */

@Slf4j
@Configuration//说明这个是SpringBoot的配置类
//让这个类继承WebMvcConfigurationSupport
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * 重写WebMvcConfigurationSupport中的addResourceHandlers方法
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //调用registry的addResourceHandler添加资源处理器，
        //再调用addResourceLocations添加资源路径（classpath对应的是resource目录）
        //后台
        log.info("开始进行静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        //前台
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展Mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象，作用：
        //将我们controller作用返回结果把它转成相应的json
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);//需要把我们的转换器放到最前面：下标是0，优先使用
    }
}

