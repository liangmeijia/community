package cn.mj.community.service.impl;

import cn.mj.community.dao.CommentMapper;
import cn.mj.community.pojo.Comment;
import cn.mj.community.service.CommentService;
import cn.mj.community.service.DiscussPostService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService, CommunityConst {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> findCommentsByEntityType(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCounts(int entityType, int entityId) {
        return commentMapper.selectCommentCounts(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        //1.add comment
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.sensitiveWordsFilter(comment.getContent()));
        int re=commentMapper.insertComment(comment);
        //2.update the comments of post
        if(ENTITY_TYPE_POST == comment.getEntityType()){
            int commentCounts = commentMapper.selectCommentCounts(ENTITY_TYPE_POST, comment.getEntityId());
            discussPostService.updateCommentCounts(comment.getEntityId(),commentCounts);
        }


        return re;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
