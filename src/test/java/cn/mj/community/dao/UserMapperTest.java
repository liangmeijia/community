package cn.mj.community.dao;

import cn.mj.community.pojo.User;
import cn.mj.community.util.CommunityUtil;
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
        User user = userMapper.selectById(103);
        log.debug("user {}",user);

        user = userMapper.selectByUsername("aaa");
        log.debug("user {}",user);

        user = userMapper.selectByEmail("nowcoder111@sina.com");
        log.debug("user {}",user);

//        List<User> allUser = userMapper.findAllUser();
//        log.debug("all user len {}",allUser.size());

    }
    @Test
    public void testInsert(){
        User user = new User();
        user.setUsername("lmj");
        user.setPassword("123");
        user.setSalt(CommunityUtil.getUUID().substring(0,4));
        user.setEmail("lmj233@yeah.net");
        user.setType(0);
        user.setType(0);

//        userMapper.insertUser(user);
        log.debug("user {}",user);
//        userMapper.deleteByUsername(user.getUsername());
        userMapper.updateStatusById(160,1);
    }
}
