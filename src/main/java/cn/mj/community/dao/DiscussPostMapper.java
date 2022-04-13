package cn.mj.community.dao;

import cn.mj.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts (int userId,int offset,int limit);

    int selectDiscussPostRows();
}