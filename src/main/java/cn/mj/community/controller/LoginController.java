package cn.mj.community.controller;

import cn.mj.community.dao.LoginTicketMapper;
import cn.mj.community.pojo.LoginTicket;
import cn.mj.community.pojo.User;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConst {
    @Autowired
    private UserService userService;
    @Autowired
    private Producer producer;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String username, String password,String kaptcha, boolean rememberme,
//                        HttpSession session
                        @CookieValue("kaptchaOwner")String kaptchaOwner, Model model,HttpServletResponse response){
        //get kaptcha in session -> get kaptcha in redis
//        String kaptcha_ = (String) session.getAttribute("kaptcha");
        String kaptcha_ = null;
        if(kaptchaOwner!=null){
            String kaptchaKey = CommunityUtil.getKaptchaKey(kaptchaOwner);
            kaptcha_ = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        //
        if(StringUtils.isBlank(kaptcha_)){
            model.addAttribute("kaptchaMsg","kaptcha is null");
            return "/site/login";
        }
        if(!kaptcha.equalsIgnoreCase(kaptcha_)){
            model.addAttribute("kaptchaMsg","kaptcha is error");
            return "/site/login";
        }
        int expired=rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password,expired);
        if(map.containsKey("ticket")){
            //login success
            String ticket = (String) map.get("ticket");
            Cookie cookie = new Cookie("ticket", ticket);
            cookie.setPath(contextPath);
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }


    }
    @RequestMapping(value = "/loginOut",method = RequestMethod.GET)
    public String loginOut(@CookieValue("ticket") String ticket){
        userService.loginOut(ticket);
        return "redirect:/login";
    }
    @RequestMapping(value = "/register" ,method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(User user, Model model){
        Map<String, Object> map = userService.register(user);
        if(map==null){
            //register success
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(value = "/activate/{userId}/{code}",method = RequestMethod.GET)
    public String activate(@PathVariable("userId")String userId, @PathVariable("code")String activationCode,Model model){
        User user = userService.findUserById(Integer.parseInt(userId));
        if (user.getStatus()==ACTIVATE_SUCCESS){
            //repleatly register
            model.addAttribute("content","you is registered repeatedly");
            model.addAttribute("target","/index");
        }
        if(user.getActivationCode().equals(activationCode)){
            //success activate
            userService.updateStatusById(Integer.parseInt(userId),ACTIVATE_SUCCESS);
            model.addAttribute("content","you is registered successfully");
            model.addAttribute("target","/login");
        }else {
            //fail activate
            model.addAttribute("content","you is registered unsuccessfully");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";

    }

    @RequestMapping(value = "/kaptcha",method = RequestMethod.GET)
    public void kaptcha(HttpServletResponse response
//                        ,HttpSession session
    ){
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        //save kaptcha in session　－> save kaptcha in redis
        //session.setAttribute("kaptcha",text);
        //produce string 'owner'
        String owner = CommunityUtil.getUUID();
        Cookie cookie = new Cookie("kaptchaOwner", owner);
        cookie.setPath(contextPath);
        cookie.setMaxAge(60);
        response.addCookie(cookie);

        //save kaptcha in redis
        String kaptchaKey = CommunityUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setCookie")
    public void setCookie(HttpServletResponse response){
        try {
            Cookie cookie = new Cookie("ticket", CommunityUtil.getUUID());
            cookie.setPath("/community");
            cookie.setMaxAge(60*10);
            response.addCookie(cookie);
            response.getWriter().write("set cookie success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getCookie")
    public void getCookie(@CookieValue("ticket") String ticket,HttpServletResponse response){
        System.out.println(ticket);
        try {
            response.getWriter().write("get cookie success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/setSession")
    public void setSession(HttpServletResponse response, HttpSession session){
        session.setAttribute("ticket",CommunityUtil.getUUID());
        session.setAttribute("name","dyy");
        try {
            response.getWriter().write("set session success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getSession")
    public void getSession(HttpServletResponse response,HttpSession session){
        String  ticket = (String) session.getAttribute("ticket");
        String  name = (String) session.getAttribute("name");
        System.out.println(ticket);
        System.out.println(name);
        try {
            response.getWriter().write("get session success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
