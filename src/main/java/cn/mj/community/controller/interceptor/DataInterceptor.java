package cn.mj.community.controller.interceptor;

import cn.mj.community.pojo.User;
import cn.mj.community.service.DataService;
import cn.mj.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.UV
            dataService.recordUV(request.getRemoteHost());
        //2.DAU
        User user = hostHolder.getUser();
        if(user!=null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
