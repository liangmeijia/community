package cn.mj.community.util;

public interface CommunityConst {
    //activate success
    int ACTIVATE_SUCCESS=1;
    //activate failed
    int ACTIVATE_FAILED=0;
    //remember login timeout
    int REMEMBER_EXPIRED_SECONDS=1000*60*30;
    //default login timeout
    int DEFAULT_EXPIRED_SECONDS=1000*60*10;
    //
    int ENTITY_TYPE_POST=1;
    int ENTITY_TYPE_COMMENT=2;
    int ENTITY_TYPE_USER=3;
    //redis key
    String REDIS_KEY_ENTITY_PRE = "like:entity";
    String REDIS_KEY_USER_PRE = "like:user";
    String REDIS_KEY_FOLLOWEE_PRE = "followee";
    String REDIS_KEY_FOLLOWER_PRE = "follower";
    String REDIS_KEY_KAPTCHA_PRE = "kaptcha";
    String REDIS_KEY_TICKET_PRE = "ticket";
    String REDIS_KEY_USERINFO_PRE = "user";
    String SPLIT = ":";
    //kafka topic
    String KAFKA_TOPIC_COMMENT ="comment";
    String KAFKA_TOPIC_LIKE = "like";
    String KAFKA_TOPIC_FOLLOW ="follow";
    //system user id
    int SYSTEM_USER_ID = 1;
}
