package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName CommonController
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/24 07:26
 **/
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/common")
@Slf4j
@Api(tags = "文件管理")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @ApiOperation("文件下载")
    @GetMapping("/download")
    public void download(String name,HttpServletResponse response) {
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //通过输出流将文件写回浏览器，在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应类型
            response.setContentType("img/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
