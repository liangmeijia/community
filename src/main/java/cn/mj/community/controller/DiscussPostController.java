package cn.mj.community.controller;

import cn.mj.community.dao.DiscussPostMapper;
import cn.mj.community.pojo.Comment;
import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.pojo.Page;
import cn.mj.community.pojo.User;
import cn.mj.community.service.CommentService;
import cn.mj.community.service.LikeService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import cn.mj.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConst {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add",method= RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();

        if(user == null){
            throw new IllegalArgumentException("You haven't logged in yet");
        }
        if(StringUtils.isBlank(title)){
            return CommunityUtil.getJsonString(1, "title is blank");
        }
        if(StringUtils.isBlank(content)){
            return CommunityUtil.getJsonString(1,"content is blank");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        //
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);
        discussPost.setTitle(sensitiveFilter.sensitiveWordsFilter(title));
        discussPost.setContent(sensitiveFilter.sensitiveWordsFilter(content));
        discussPost.setCreateTime(new Date());
        String json = CommunityUtil.getJsonString(0,"add discussPost successfully");
        discussPostMapper.insertDiscussPost(discussPost);
        return json;
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);
        //get user info
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);
        // get post likeCount
        long postLikeCount = likeService.likeCount_entity(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("postLikeCount", postLikeCount);
        model.addAttribute("postLikeStatus", hostHolder.getUser()==null?
                0:likeService.likeStatus(ENTITY_TYPE_POST, discussPostId, hostHolder.getUser().getId()));

        //paging query
        page.setLimit(5);
        page.setPath("/discussPost/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());
        //get comment list
        List<Comment> commentList = commentService.findCommentsByEntityType(ENTITY_TYPE_POST,
                discussPost.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment: commentList){
                Map<String,Object> commentVo = new HashMap<>();
                //comment
                commentVo.put("comment",comment);
                //author
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //get comment likeCount
                long commentLikeCount = likeService.likeCount_entity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("commentLikeCount",commentLikeCount);
                commentVo.put("commentLikeStatus", hostHolder.getUser()==null?
                        0:likeService.likeStatus(ENTITY_TYPE_COMMENT, comment.getId(), hostHolder.getUser().getId()));

                //reply list
                List<Comment> replyList = commentService.findCommentsByEntityType(ENTITY_TYPE_COMMENT,
                        comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //reply
                        replyVo.put("reply",reply);
                        //user
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //get reply likeCount
                        long replyLikeCount = likeService.likeCount_entity(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("replyLikeCount", replyLikeCount);
                        replyVo.put("replyLikeStatus", hostHolder.getUser()==null?0:
                                likeService.likeStatus(ENTITY_TYPE_COMMENT, reply.getId(), hostHolder.getUser().getId()));
                        //target
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys",replyVoList);

                int replyCounts = commentService.findCommentCounts(ENTITY_TYPE_COMMENT, comment.getId());

                commentVo.put("replyCounts",replyCounts);

                commentVoList.add(commentVo);

            }
        }

        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}
