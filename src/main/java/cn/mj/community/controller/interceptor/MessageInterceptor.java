package cn.mj.community.controller.interceptor;

import cn.mj.community.pojo.User;
import cn.mj.community.service.MessageService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor, CommunityConst {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            modelAndView.addObject("allMessageCount",
                    messageService.findUnReadNoticeCount(null, user.getId())+
                    messageService.findUnreadLetterCount(user.getId(), null));

        }
    }
}
