package cn.mj.community.dao;

import cn.mj.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts (int userId,int offset,int limit, int orderModel);

    int selectDiscussPostRows();

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCounts(int id, int commentCount);

    int updateDiscussPostScore(int id, double score);

    int updateDiscussPostType(int id, int type);

    int updateDiscussPostStatus(int id , int status);

}
