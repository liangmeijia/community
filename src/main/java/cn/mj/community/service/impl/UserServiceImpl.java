package cn.mj.community.service.impl;

import cn.mj.community.dao.UserMapper;
import cn.mj.community.pojo.User;
import cn.mj.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findUserById(int id) {
        return userMapper.findUserById(id);
    }

    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }
}
