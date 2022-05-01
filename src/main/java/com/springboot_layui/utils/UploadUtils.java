package com.springboot_layui.utils;
//上传图片用的工具类
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UploadUtils {

    //定义一个目标路径,就是我们要把图片上传到的位置;
    private  static final String BASE_PATH="F:\\apache-tomcat-8.0.50\\webapps\\upload\\";

    //定义文件服务器的访问地址
    private static  final String SERVER_PATH="http://localhost:8080/upload/";
    public static String upload(MultipartFile file){
        //获得上传文件的名称
        String filename = file.getOriginalFilename();
        System.out.println("图片的原始名字: " + filename);
        //创建UUID,用来保持文件名字的唯一性
        String uuid = UUID.randomUUID().toString().replace("-","");
        //uuid太长,截一部分
        uuid=uuid.substring(27);
        //进行文件名称的拼接
        String newFileName = uuid+"-"+filename;
        //创建文件实例对象
        File uploadFile = new File(BASE_PATH,newFileName);
        //判断当前文件是否存在
        if (!uploadFile.exists()){
            //如果不存在就创建一个文件夹
            uploadFile.mkdirs();
        }
        //执行文件上传的命令
        try {
            file.transferTo(uploadFile);
        } catch (IOException e) {
            return null;
        }
        //将上传的文件名称返回,注意: 这里我们返回一个 服务器地址
        return SERVER_PATH+newFileName;
    }

    public static String uploads(MultipartFile file){
        //获得上传文件的名称
        String filename = file.getOriginalFilename();
        System.out.println("图片的原始名字: " + filename);
        //创建UUID,用来保持文件名字的唯一性
        String uuid = UUID.randomUUID().toString().replace("-","");
        //uuid太长,截一部分
        uuid=uuid.substring(27);
        //进行文件名称的拼接
        String newFileName = uuid+".png";
        //创建文件实例对象
        File uploadFile = new File(BASE_PATH,newFileName);
        //判断当前文件是否存在
        if (!uploadFile.exists()){
            //如果不存在就创建一个文件夹
            uploadFile.mkdirs();
        }
        //执行文件上传的命令
        try {
            file.transferTo(uploadFile);
        } catch (IOException e) {
            return null;
        }
        //将上传的文件名称返回,注意: 这里我们返回一个 服务器地址
        return SERVER_PATH+newFileName;
    }

}
