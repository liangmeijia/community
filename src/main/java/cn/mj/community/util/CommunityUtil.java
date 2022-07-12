package cn.mj.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil implements CommunityConst{

    //product random string
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //md5
    public static String md5(String str){
        if(StringUtils.isBlank(str)){
            return null;
        }
        String md5 = DigestUtils.md5DigestAsHex(str.getBytes());
        return md5;
    }

    //get cookie from request
    public static String getCookieValue(HttpServletRequest request,String cookieName){
        if(request ==null || cookieName == null){
            throw new IllegalArgumentException("argument is illegal ");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie:cookies) {
                if(cookieName.equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    //product json string
    public static String getJsonString(int code, String msg, Map<String,Object> map){
        JSONObject json =new JSONObject();
        json.put("code",code);
        if(!StringUtils.isBlank(msg)){
            json.put("msg",msg);
        }

        if(map!=null){
            for(Object key:map.keySet()){
                json.put(key.toString(),map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJsonString(int code, String msg){
        return getJsonString(code,msg,null);
    }
    public static String getJsonString(int code){
        return getJsonString(code,null,null);
    }

    //produce key of redis
    public static String getRedisKey(int entityType, int entityId){
        //like:entity:entityType:entityId->set(userId)
        return REDIS_KEY_ENTITY_PRE+SPLIT+entityType+SPLIT+entityId;
    }
    public static String getRedisKey( int userid){
        //like:user:userId->String()
        return REDIS_KEY_USER_PRE+SPLIT+userid;
    }

    public static String getFolloweeKey(int userId,int entityType){
        //followee:userId:entityType->zset(entityId,now)
        return REDIS_KEY_FOLLOWEE_PRE+SPLIT+userId+SPLIT+entityType;
    }

    public static String getFollowerKey(int entityType, int entityId){
        //follower:entityType:entityId->zset(userId,now)
        return REDIS_KEY_FOLLOWER_PRE+SPLIT+entityType+SPLIT+entityId;
    }

    public static String getKaptchaKey(String owner){
        //kaptcha:owner
        return REDIS_KEY_KAPTCHA_PRE+SPLIT+owner;
    }

    public static String getTicketKey(String ticket){
        //ticket:ticket
        return REDIS_KEY_TICKET_PRE+SPLIT+ticket;
    }

    public static String getUserKey(int userId){
        //ticket:ticket
        return REDIS_KEY_USERINFO_PRE+SPLIT+userId;
    }

    public static String getPostScoreKey(){
        //post:score -> set(postId1, postId2...)
        return REDIS_KEY_POST_PRE+SPLIT+"score";
    }

    //daily UV
    public static String getUVKey(String date){
        return REDIS_KEY_UV +SPLIT+date;
    }
    //range UV
    public static String getUVKey(String startDate, String endDate){
        return REDIS_KEY_UV+SPLIT+startDate+SPLIT+endDate;
    }
    //daily DAU
    public static String getDAUKey(String date){
        return REDIS_KEY_DAU +SPLIT+date;
    }
    //range DAU
    public static String getDAUKey(String startDate, String endDate){
        return REDIS_KEY_DAU+SPLIT+startDate+SPLIT+endDate;
    }
    //for test
    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","bob");
        map.put("age","22");

        String re = getJsonString(0, "failed",map);
        System.out.println(re);
    }
}
