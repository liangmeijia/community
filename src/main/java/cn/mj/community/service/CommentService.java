package cn.mj.community.service;

import cn.mj.community.pojo.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByEntityType(int entityType, int entityId, int offset, int limit);
    int findCommentCounts(int entityType, int entityId);
    int addComment(Comment comment);
    Comment findCommentById(int id);
}
