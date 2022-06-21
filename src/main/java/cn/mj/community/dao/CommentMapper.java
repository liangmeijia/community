package cn.mj.community.dao;

import cn.mj.community.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCommentCounts(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
