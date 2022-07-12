package cn.mj.community.controller;

import cn.mj.community.annotation.LoginRequiredAnnotation;
import cn.mj.community.pojo.User;
import cn.mj.community.service.FollowService;
import cn.mj.community.service.LikeService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConst {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private String uploadLocation;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Value("${qiniu.key.access}")
    private String accessKey;
    @Value("${qiniu.key.secret}")
    private String secretKey;
    @Value("${qiniu.bucket.header.name}")
    private String bucketHeaderName;
    @Value("${qiniu.bucket.header.url}")
    private String bucketHeaderUrl;

    @LoginRequiredAnnotation
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(Model model){
        //1.the filename of upload file
        String filename = CommunityUtil.getUUID();

        //2.set response info
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJsonString(0));

        //3.create upload auth token
        Auth auth = Auth.create(accessKey,secretKey);
        String uploadToken = auth.uploadToken(bucketHeaderName, filename, 60 * 60, policy);

        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("filename",filename);
        return "/site/setting";
    }

    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String filename){
        if(StringUtils.isBlank(filename)){
            logger.error("filename is null");
            return CommunityUtil.getJsonString(1,"filename is null");
        }
        //
        String url = bucketHeaderUrl+"/"+filename;
        userService.updateHeaderUrlById(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJsonString(0);
    }

    //Deprecated
    @LoginRequiredAnnotation
    @RequestMapping("/uploadHeaderUrl")
    public String updateHeaderUrl(MultipartFile headerImg, Model model){
        if(headerImg == null){
            model.addAttribute("error","file is null");
            return "/site/setting";
        }
        String originalFilename = headerImg.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if(".png".equals(fileType) || ".jpg".equals(fileType) || ".jpeg".equals(fileType)){
            String format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String newFileName = CommunityUtil.getUUID()+format+fileType;
            try {
                headerImg.transferTo(new File(uploadLocation+"/"+newFileName));
            } catch (IOException e) {
                logger.error("save headImg is wrong {}",e.getMessage());
                throw new RuntimeException("save headImg is wrong");
            }
            //upload user headerurl
            String hearUrl= domain+contextPath+"/user/header/"+newFileName;
            userService.updateHeaderUrlById(hostHolder.getUser().getId(),hearUrl);
            return "redirect:/index";
        }else{
            model.addAttribute("error","upload the type of file is illegal");
            return "/site/setting";
        }
    }

    //Deprecated
    @RequestMapping("/header/{url}")
    public void getHeaderImg(@PathVariable("url") String url, HttpServletResponse response){
        String location = uploadLocation+"/"+url;
        String type = url.substring(url.lastIndexOf("."));
        response.setContentType("image/"+type);
        try(
                FileInputStream fo = new FileInputStream(location);
                ServletOutputStream os = response.getOutputStream();
                ){
            byte[] bytes =new byte[1024];
            int b=0;
            while((b=fo.read(bytes))!=-1){
                os.write(bytes,0,b);
            }
        }catch (IOException e){
            logger.error("visit headerImg wrong{}",e.getMessage());
            throw new RuntimeException("visit headerImg wrong");
        }

    }

    @LoginRequiredAnnotation
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("the user is not exist");
        }
        model.addAttribute("user", user);
        int likeCount = likeService.likeCount_user(userId);
        model.addAttribute("likeCount",likeCount);
        //followee count
        long followeeCount = followService.followeeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //follower count
        long followerCount = followService.followerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //follow status
        boolean followStatus = false;
        if (hostHolder.getUser()!=null){
            followStatus = followService.followStatus(ENTITY_TYPE_USER, userId, hostHolder.getUser().getId());
        }
        model.addAttribute("followStatus",followStatus);
        return "/site/profile";
    }

    @LoginRequiredAnnotation
    @RequestMapping(value = "/updatePassWord",method = RequestMethod.POST)
    public String updatePassword(String oldPassWord, String newPassWord, String confirmPassWord, Model model){
        if(StringUtils.isBlank(oldPassWord)){
            model.addAttribute("oldPassWordError","oldPassWord is empty");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassWord)){
            model.addAttribute("newPassWordError","newPassWord is empty");
            return "/site/setting";
        }
        if(StringUtils.isBlank(confirmPassWord)){
            model.addAttribute("confirmPassWordError","confirmPassWordError is empty");
            return "/site/setting";
        }
        if(!confirmPassWord.equals(newPassWord)){
            model.addAttribute("confirmPassWordError","the password is different");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        if(!user.getPassword().equals(CommunityUtil.md5(oldPassWord+user.getSalt()))){
            model.addAttribute("oldPassWordError","oldPassWord is wrong");
            return "/site/setting";
        }
        userService.updatePassWordById(user.getId(),CommunityUtil.md5(newPassWord+user.getSalt()));

        return "redirect:/index";
    }
}
