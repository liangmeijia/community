package cn.mj.community.controller.interceptor;

import cn.mj.community.pojo.LoginTicket;
import cn.mj.community.pojo.User;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CommunityUtil.getCookieValue(request, "ticket");
        if(ticket!=null){
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
            if(loginTicket!=null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //login success
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.addUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null&& modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
