package cn.mj.community.service;

import cn.mj.community.pojo.LoginTicket;
import cn.mj.community.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User findUserById(int id);
    List<User> findAllUser();
    Map<String,Object> register (User user);
    Map<String ,Object> login(String username,String password,int expired);
    int updateStatusById(int id,int status);
    void loginOut(String ticket);

}
