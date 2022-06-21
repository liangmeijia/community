package cn.mj.community.controller;

import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.pojo.Page;
import cn.mj.community.pojo.User;
import cn.mj.community.service.DiscussPostService;
import cn.mj.community.service.LikeService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConst {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String  index(Model model, Page page){
        int offset = page.getOffset();
        int limit = page.getLimit();
        page.setRows(discussPostService.selectDiscussPostRows());
        page.setPath("/index");

        List<Map<String,Object>> discussPosts=new ArrayList<>();
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, offset, limit);
        for (DiscussPost discussPost : list) {
            Map<String,Object> map=new HashMap<>();
            //get post likeCount
            long postLikeCount = likeService.likeCount_entity(ENTITY_TYPE_POST, discussPost.getId());
            map.put("postLikeCount", postLikeCount);
            //
            map.put("discussPost",discussPost);
            User user = userService.findUserById(discussPost.getUserId());
            map.put("user",user);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
        return "index";
    }
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

}
