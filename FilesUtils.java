package cn.shining.art.sys.tools;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Created by kfc on 2017/4/25.
 */
public class FilesUtils {

    public static Integer UPLOADWORK = 1;
    public static Integer UPLOADPHOTO = 2;

    /*0文档 1 图片 2视频 3美术作品 4美术展览*/
    public static Integer WORK_DOC = 0;
    public static Integer WORK_IMG = 1;
    public static Integer WORK_VIO = 2;
    public static Integer WORK_ART = 3;
    public static Integer WORK_EXH = 4;

    private static Logger logger = LoggerFactory.getLogger(FilesUtils.class);

    static  RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

    static  HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();;


    private static String getHttpServletRequest(String SERVICEPATH,String ROOT) {
        /*String url = servletRequest.getRequestURL().toString();
        Integer port = servletRequest.getServerPort() ;

        StringBuffer stringBuffer = new StringBuffer();
        String curPath = url.split(port+"")[0];
        stringBuffer.append(curPath).append(port).append("/").append(ROOT);
        return stringBuffer.toString();
        */
        return SERVICEPATH+ROOT;
    }

    public static Map<String, String> parseJson2Map(Object obj){
        Map<String,String> map = (Map)obj;
        map.remove("fty");
        map.remove("thumb");
        map.remove("view");
        map.remove("resolution");

        String tmp = map.toString().replace("\"", "")
                .replace("[{", "")
                .replace("}]", "")
                .replace("{","")
                .replace("}","");
        String[] tmps = tmp.split(":http");
        map.clear();
        map.put("name",tmps[0]);
        map.put("path","http"+tmps[1]);
        return map;
    }

    public static String upload(MultipartFile[] files,String SERVICEPATH,String PATH, String ROOT, Integer udt) throws IllegalStateException, IOException {
        //创建一个通用的多部分解析器
        String fileName = "";
        String tagName = "";
        PATH = getHttpServletRequest(SERVICEPATH,PATH);
        if (null == files || files.length <= 0)
            return null;
        List<Map> nameList = new ArrayList<>();
        for (MultipartFile tmp : files) {

            fileName = tmp.getOriginalFilename();
            Map<String, String> map = new HashMap<>();
            if (!StringUtils.isEmpty(fileName)) {
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                Path path = null;
                switch (suffix) {
                    case ".png":
                    case ".jpg":
                    case ".jpeg":
                        map.put("fty", WORK_IMG+"");
                        if (UPLOADPHOTO == udt)
                            path = Paths.get(ROOT, "img" + File.separator + DateUtils.getCurrentDate());
                        if (UPLOADWORK == udt)
                            path = Paths.get(ROOT, "works" + File.separator +"imgs" + File.separator + DateUtils.getCurrentDate());
                        break;
                    case ".doc":
                    case ".docx":
                        map.put("fty", WORK_DOC+"");
                        path = Paths.get(ROOT, "doc" + File.separator + DateUtils.getCurrentDate());
                        break;
                    default:
                        map.put("fty", WORK_VIO+"");
                        path = Paths.get(ROOT, "works" + File.separator +"videos" + File.separator + DateUtils.getCurrentDate());
                        break;
                }

                if (!Files.isExecutable(path)) {
                    Files.createDirectories(path);
                }
                long systime = new Date().getTime();
                tagName = systime + suffix;

                Files.copy(tmp.getInputStream(), Paths.get(path.toString() + File.separator + tagName));

                if(map.get("fty").equals(WORK_IMG+"")){
                    Image src = javax.imageio.ImageIO.read(tmp.getInputStream());
                    int wideth=src.getWidth(null); //得到源图宽
                    int height=src.getHeight(null); //得到源图长
                    String thumbName = systime+"_x"+ suffix;
                    String viewName = systime+"_1"+ suffix;
                    ImageUtils.reduceImage(Paths.get(path.toString() + File.separator + tagName).toString(),
                            Paths.get(path.toString() + File.separator + viewName).toString());
                    ImageUtils.reduceImageDefault(Paths.get(path.toString() + File.separator + tagName).toString(),
                            Paths.get(path.toString() + File.separator + thumbName).toString());
                    map.put("thumb", PATH+"/"+thumbName);
                    map.put("view", PATH+"/"+viewName);
                    map.put("resolution", wideth+"x"+height);
                }else{
                    map.put("thumb", "");
                    map.put("view", "");
                    map.put("resolution", "");
                }

                map.put(fileName + "_" +System.currentTimeMillis(), PATH+"/"+tagName);

                nameList.add(map);
            }

        }

        return JSON.toJSONString(nameList);
    }

}
