package cn.mj.community.controller;

import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.pojo.Page;
import cn.mj.community.service.ElasticsearchService;
import cn.mj.community.service.LikeService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ElasticsearchController implements CommunityConst {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyWords, Page page, Model model){


        Map<String,Object> map = elasticsearchService.selectDiscussPosts(keyWords, page.getCurrent()-1, page.getLimit());

        List<DiscussPost> discussPostList = (List<DiscussPost>) map.get("discussPosts");
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(discussPostList!=null){

            for(DiscussPost discussPost:discussPostList){
                Map<String, Object> map1 = new HashMap<>();
                //post
                map1.put("post",discussPost);
                //user
                map1.put("user",userService.findUserById(discussPost.getUserId()));
                //like
                map1.put("likeCount",likeService.likeCount_entity(ENTITY_TYPE_POST,discussPost.getId()));
                discussPosts.add(map1);
            }
        }

        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyWords",keyWords);//for cache in html
        //set page
        page.setPath("/search?keyWords="+keyWords);
        page.setRows(map==null?0: Integer.parseInt(map.get("count").toString()));

        return "/site/search";
    }
}
