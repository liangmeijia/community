package cn.mj.community.service;

import cn.mj.community.pojo.User;

import java.util.List;

public interface UserService {
    User findUserById(int id);
    List<User> findAllUser();
}
