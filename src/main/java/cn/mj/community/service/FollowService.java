package cn.mj.community.service;

import java.util.List;
import java.util.Map;

public interface FollowService {
    void follow(int entityType, int entityId, int userId);
    long followeeCount(int userId, int entityType);
    long followerCount(int entityType, int entityId);
    boolean followStatus(int entityType, int entityId, int userId);

    List<Map<String,Object>> findFollowees(int userId, int entityType, int offset, int limit);
    List<Map<String, Object>> findFollowers(int entityType, int entityId, int offset, int limit);

}
