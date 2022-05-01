package com.springboot_layui.controller;
//图片操作控制层(一定要提前打开本机的Tomcat服务器)
/* 想要部署到linux上,把   "F:\\apache-tomcat-8.0.50\\webapps\\upload\\"
                  改成   "/opt/tomcat/apache-tomcat-9.0.59/webapps/upload/" 即可    */
import com.springboot_layui.entity.DataJson;
import com.springboot_layui.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("upload")
public class UploadController {

    @Autowired
    StringRedisTemplate redisTemplate;


    //把上传的图片保存到本机的Tomcat上
    @RequestMapping("image")
    @ResponseBody
    public DataJson image(MultipartFile file){
        //调用工具类完成文件上传,获取文件在服务器中的位置
        String imagePath = UploadUtils.upload(file);
        //打印文件在服务器中的名字
        System.out.println("图片地址: "+imagePath);

        //参加Json格式的对象
        DataJson dataJson = new DataJson();
        if (imagePath != null){
            //创建一个HashMap用来存放图片路径
            HashMap hashMap = new HashMap();
            hashMap.put("src",imagePath);
            //Json设置属性值
            dataJson.setCode(1);
            dataJson.setMsg("上传成功");
            dataJson.setData(hashMap);
        }else{
            dataJson.setCode(0);
            dataJson.setMsg("上传失败");
        }
        //返回Json
        return dataJson;
    }

    //添加到我的图片库
    @RequestMapping("addImage")
    @ResponseBody  //注解作用:表单里的域值都能在参数列表中获取,只不过参数名要与域名一致
    public String addImage(String imageDescribe,String imagePath){

        HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();

        //通过图片访问路径截取到图片名
        String imageName=imagePath.substring(29);

        //redis:通过指定的key放入传来的 图片名称与图片描述
        operations.put("King",imageName,imageDescribe);

        System.out.println("图片描述:"+imageDescribe);
        System.out.println("图片名字:"+imageName);
        return "1";
    }


    //查看我的图片库
    @RequestMapping("myImage")
    public String myImage(Model model){
        HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();
        //将key中的域与值转成Map
        Map<Object, Object> king = operations.entries("King");
        //Map转成Set集合,这样Map里的key与value就能被提取出来
        Set<Map.Entry<Object, Object>> kings = king.entrySet();
        //设置参数
        model.addAttribute("Kings",kings);
        return "myImage";
    }


    //通过图片名字描述删除指定图片
    @RequestMapping("/delete/{imageName}")
    public String deleteImage(@PathVariable("imageName") String imageName){
        System.out.println("要删除的图片名: " + imageName);

        HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();
        //通过图片名字从redis中删除一组图片与描述
        operations.delete("King",imageName);
        
        //图片名字拼接路径 成为在服务器中的访问路径
        String imagePath="http://localhost:8080/upload/"+imageName;

        //文件操作,根据地址与图片名 删除在本机上的图片
        File file = new File("F:\\apache-tomcat-8.0.50\\webapps\\upload\\"+imageName);
        file.delete();
        //重定向到我的图片库
        return "redirect:/upload/myImage";
    }
}