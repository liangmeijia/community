package cn.mj.community.service.impl;

import cn.mj.community.pojo.User;
import cn.mj.community.service.FollowService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public void follow(int entityType, int entityId, int userId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                String redisFolloweeKey = CommunityUtil.getFolloweeKey(userId,entityType);
                String redisFollowerKey = CommunityUtil.getFollowerKey(entityType, entityId);

                Double score = redisTemplate.opsForZSet().score(redisFolloweeKey, entityId);
                operations.multi();
                if(score==null){
                    //the user is not follow the entity
                    redisTemplate.opsForZSet().add(redisFolloweeKey, entityId,System.currentTimeMillis());
                    redisTemplate.opsForZSet().add(redisFollowerKey,userId,System.currentTimeMillis());
                }else{
                    //the user followed the entity
                    redisTemplate.opsForZSet().remove(redisFolloweeKey, entityId);
                    redisTemplate.opsForZSet().remove(redisFollowerKey,userId);
                }
                return operations.exec();
            }
      });
    }

    @Override
    public long followeeCount(int userId, int entityType) {
        String redisFolloweeKey = CommunityUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(redisFolloweeKey);
    }

    @Override
    public long followerCount(int entityType, int entityId) {
        String redisFollowerKey = CommunityUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(redisFollowerKey);
    }

    @Override
    public boolean followStatus(int entityType, int entityId, int userId) {
        String redisFolloweeKey = CommunityUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(redisFolloweeKey,entityId)!=null;
    }

    @Override
    public List<Map<String, Object>> findFollowees(int userId, int entityType, int offset, int limit) {
        String redisFolloweeKey = CommunityUtil.getFolloweeKey(userId,entityType);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(redisFolloweeKey, offset, offset + limit - 1);

        if(set == null){
            return null;
        }
        List<Map<String, Object>> userList = new ArrayList<>();

        for(Integer uId:set){
            Map<String, Object> map = new HashMap<>();
            map.put("user",userService.findUserById(uId));
            map.put("followTime",new Date(redisTemplate.opsForZSet().score(redisFolloweeKey,uId).longValue()));
            userList.add(map);
        }
        return userList;
    }

    @Override
    public List<Map<String, Object>> findFollowers(int entityType, int entityId, int offset, int limit) {
        String redisFollowerKey = CommunityUtil.getFollowerKey(entityType,entityId);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(redisFollowerKey, offset, offset + limit - 1);

        if(set == null){
            return null;
        }
        List<Map<String, Object>> userList = new ArrayList<>();

        for(Integer uId:set){
            Map<String, Object> map = new HashMap<>();
            map.put("user",userService.findUserById(uId));
            map.put("followTime",new Date(redisTemplate.opsForZSet().score(redisFollowerKey,uId).longValue()));
            userList.add(map);
        }
        return userList;
    }
}
