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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String  index(Model model, Page page,
                         @RequestParam(name = "orderModel",defaultValue = "0") int orderModel){
        int offset = page.getOffset();
        int limit = page.getLimit();
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderModel="+orderModel);

        List<Map<String,Object>> discussPosts=new ArrayList<>();
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, offset, limit, orderModel);
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
        model.addAttribute("orderModel",orderModel);

        return "index";
    }
    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }

}
