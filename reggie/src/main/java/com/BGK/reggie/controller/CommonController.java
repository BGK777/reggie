package com.BGK.reggie.controller;

import com.BGK.reggie.common.R;
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
 * 通用控制器
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    //文件下载存放目录
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件下载，下载到本地磁盘
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID避免重名
        String fileName = UUID.randomUUID().toString() + suffix;
        //检查文件夹是否存在，不存在则创建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            //保存下载的文件
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件上传，回显至浏览器
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //输出流，通过输出流把文件写回浏览器，在浏览器显示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            //读写操作
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
