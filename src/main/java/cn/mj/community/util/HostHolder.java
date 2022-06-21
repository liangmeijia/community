package cn.mj.community.util;

import cn.mj.community.pojo.User;
import org.springframework.stereotype.Component;

/**
 * save login user
 */
@Component
public class HostHolder {
    private ThreadLocal<User> threadLocal = new ThreadLocal<>();
    public void addUser(User user){
        threadLocal.set(user);
    }
    public User getUser(){
        return threadLocal.get();
    }
    public void clear(){
        threadLocal.remove();
    }
}
