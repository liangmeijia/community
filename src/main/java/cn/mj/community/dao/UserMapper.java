package cn.mj.community.dao;

import cn.mj.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findUserById(int id);
    List<User> findAllUser();
}
