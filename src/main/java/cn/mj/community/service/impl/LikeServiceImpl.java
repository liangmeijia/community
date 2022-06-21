package cn.mj.community.service.impl;

import cn.mj.community.service.LikeService;
import cn.mj.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //like
    @Override
    public void like(int entityType, int entityId, int userId, int targetId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                String redisEntityKey = CommunityUtil.getRedisKey(entityType, entityId);
                String redisUserKey = CommunityUtil.getRedisKey(targetId);
                boolean isMember = redisTemplate.opsForSet().isMember(redisEntityKey, userId);
                //start redis transaction
                operations.multi();
                if(isMember){
                    //liked
                   redisTemplate.opsForSet().remove(redisEntityKey, userId);
                   redisTemplate.opsForValue().decrement(redisUserKey);
                }else{
                    //no liked
                    redisTemplate.opsForSet().add(redisEntityKey, userId);
                    redisTemplate.opsForValue().increment(redisUserKey);
                }
                return operations.exec();
            }
        });
    }
    //the number of like for entity
    @Override
    public long likeCount_entity(int entityType, int entityId) {
        String redisEntityKey = CommunityUtil.getRedisKey(entityType, entityId);
        return redisTemplate.opsForSet().size(redisEntityKey);
    }
    //the status of like for entity
    // 1 -> like
    // 0 -> no like
    @Override
    public int likeStatus(int entityType, int entityId, int userId) {
        String redisEntityKey = CommunityUtil.getRedisKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(redisEntityKey, userId);
        if(isMember){
            return 1;
        }else{
            return 0;
        }
    }
    //the number of like for user
    @Override
    public int likeCount_user(int userId) {
        String redisUserKey = CommunityUtil.getRedisKey(userId);
        Object likeCount_user = redisTemplate.opsForValue().get(redisUserKey);
        return likeCount_user==null?0: (int) likeCount_user;
    }
}
