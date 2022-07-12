package cn.mj.community.service.impl;

import cn.mj.community.service.DataService;
import cn.mj.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    public void recordUV(String IP){
        String key = CommunityUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(key,IP);
    }

    public long calculateUV(Date startDate, Date endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("params is null");
        }

        List<String> keys = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)){
            String key = CommunityUtil.getUVKey(df.format(calendar.getTime()));
            keys.add(key);
            calendar.add(Calendar.DATE,1);
        }
        //union key
        String unionKey = CommunityUtil.getUVKey(df.format(startDate),df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(unionKey, keys.toArray());

        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    public void recordDAU(int userId){
        String key = CommunityUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(key,userId,true);
    }

    public long calculateDAU(Date startDate, Date endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("params is null");
        }

        List<byte[]> keys = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)){
            String key = CommunityUtil.getDAUKey(df.format(calendar.getTime()));
            keys.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String unionKey = CommunityUtil.getDAUKey(df.format(startDate), df.format(endDate));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        unionKey.getBytes(),keys.toArray(new byte[0][0]));
                return connection.bitCount(unionKey.getBytes());
            }
        });

    }
}
