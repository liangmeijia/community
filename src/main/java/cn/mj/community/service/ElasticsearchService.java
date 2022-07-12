package cn.mj.community.service;

import cn.mj.community.pojo.DiscussPost;

import java.util.List;
import java.util.Map;

public interface ElasticsearchService {
    void addDiscusPost(DiscussPost discussPost);
    void deleteDiscussPost(int id);
    Map<String,Object> selectDiscussPosts(String keyword, int current, int limit);
}
