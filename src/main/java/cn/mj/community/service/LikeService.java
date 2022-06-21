package cn.mj.community.service;

public interface LikeService {
    void like(int entityType, int entityId, int userId, int targetId);
    long likeCount_entity(int entityType, int entityId);
    int likeStatus(int entityType, int entityId, int userId);
    int likeCount_user(int userId);
}
