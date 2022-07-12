package cn.mj.community.service.impl;

import cn.mj.community.dao.DiscussPostMapper;
import cn.mj.community.pojo.Comment;
import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.service.DiscussPostService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Value("${caffeine.posts.maxSize}")
    private int maxSize;

    @Value("${caffeine.posts.expireSeconds}")
    private int  expireSeconds;

    //Caffeine core interface　：　Cache , LoadingCache, AsyncLoadingCache
    private LoadingCache<String,List<DiscussPost>> discussPostCache;
    private LoadingCache<String,Integer> discussPostRowsCache;

    @PostConstruct
    public void init(){
        discussPostCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        //1.
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("params is null");
                        }
                        String[] params = key.split(":");
                        if(params == null || params.length !=2){
                            throw new IllegalArgumentException("params is error");
                        }
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);
                        // 二级缓存: Redis -> mysql

                        logger.debug("load discussPostList in DB.");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        discussPostRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull String key) throws Exception {
                        if(key == null){
                            throw new IllegalArgumentException("params is null");
                        }
                        logger.debug("load discussPostList in DB.");
                        return discussPostMapper.selectDiscussPostRows();
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderModel) {
        if(userId == 0 && orderModel == 1){
            return discussPostCache.get(offset+":"+limit);//85->227
        }
        logger.debug("load discussPostList in DB.");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderModel);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        if(userId == 0){
            return discussPostRowsCache.get(String.valueOf(userId));
        }
        logger.debug("load discussPostList in DB.");
        return discussPostMapper.selectDiscussPostRows();
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
       return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCounts(int id,  int commentCount) {
        return discussPostMapper.updateCommentCounts(id,commentCount);
    }

    @Override
    public int updateDiscussPostScore(int id, double score) {
        return discussPostMapper.updateDiscussPostScore(id, score);
    }

    @Override
    public int updateDiscussPostType(int id, int type) {
        return discussPostMapper.updateDiscussPostType(id, type);
    }

    @Override
    public int updateDiscussPostStatus(int id, int status) {
        return discussPostMapper.updateDiscussPostStatus(id, status);
    }
}
