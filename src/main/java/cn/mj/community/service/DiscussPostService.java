package cn.mj.community.service;

import cn.mj.community.pojo.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    int selectDiscussPostRows();
}
