package com.springboot_layui.controller;
//牛的: http://localhost:8081/uploads/s
import com.springboot_layui.entity.DataJson;
import com.springboot_layui.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
@Controller
@RequestMapping("uploads")
public class UploadControllers {

    @Autowired
    StringRedisTemplate redisTemplate;

    //转到改过牛的首页
    @RequestMapping("s")
   public String indexs(){
       return "indexs";
   }

    //把上传的图片保存到本机的Tomcat上
    @RequestMapping("images")
    @ResponseBody
    public DataJson image(MultipartFile file){
        //调用工具类完成文件上传,获取文件在服务器中的位置
        String imagePath = UploadUtils.uploads(file);
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

    //查看我的图片库
    @RequestMapping("myImages")
    public String myImage(Model model){
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> gbs = operations.reverseRangeByScoreWithScores("gb", 0, 9999999 * 999999);

        //设置参数
        model.addAttribute("gbs",gbs);
        return "myImages";
    }

    //添加到我的图片库
    @RequestMapping("addImages")
    @ResponseBody  //注解作用:表单里的域值都能在参数列表中获取,只不过参数名要与域名一致
    public String addImages(String imageDescribe,String imagePath){

        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();

        //通过图片访问路径截取到图片名
        String imageName=imagePath.substring(29);

        //redis:通过指定的key放入传来的 图片名称与图片描述,初始评分是0
        operations.add("gb",imageName+"\" "+imageDescribe+" \"",0);

        System.out.println("图片描述:"+imageDescribe);
        System.out.println("图片名字:"+imageName);
        return "1";
    }

    //通过key给图片点赞+1
    @GetMapping("/call/{photo}")
    public String call(@PathVariable("photo")String photo, Model model){
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        operations.incrementScore("gb",photo,1);
        return "redirect:/uploads/myImages";
    }

    //通过图片名字描述删除指定图片
    @GetMapping("/delete/{photo}")
    public String delete(@PathVariable("photo")String photo, Model model){
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        operations.remove("gb",photo);
         //截取出图片名
        String imageName = photo.substring(0, 9);
        //删除图片在本机中的位置
        File file = new File("F:\\apache-tomcat-8.0.50\\webapps\\upload\\"+imageName);
        file.delete();
        return "redirect:/uploads/myImages";
    }

}
