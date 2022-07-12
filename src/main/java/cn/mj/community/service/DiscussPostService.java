package cn.mj.community.service;

import cn.mj.community.pojo.Comment;
import cn.mj.community.pojo.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderModel);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(int id);

    int updateCommentCounts(int id, int commentCount);

    int updateDiscussPostScore(int id , double score);

    int updateDiscussPostType(int id, int type);

    int updateDiscussPostStatus(int id , int status);
}
