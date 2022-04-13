package cn.mj.community.dao;

import cn.mj.community.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        User user = userMapper.findUserById(103);
        log.debug("user {}",user);

        List<User> allUser = userMapper.findAllUser();
        log.debug("all user len {}",allUser.size());

    }
}
