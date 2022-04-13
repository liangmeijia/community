package cn.mj.community.service.impl;

import cn.mj.community.dao.DiscussPostMapper;
import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {

        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    @Override
    public int selectDiscussPostRows() {
        return discussPostMapper.selectDiscussPostRows();
    }
}
