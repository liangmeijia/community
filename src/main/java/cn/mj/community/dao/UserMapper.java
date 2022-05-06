package cn.mj.community.dao;

import cn.mj.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(int id);
    List<User> findAllUser();
    User selectByUsername(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int deleteByUsername(String username);
    int updateStatusById(int id,int status);
    int updateHeaderUrlById(int id,String headerUrl);
    int updatePassWordById(int id,String password);
}
