package cn.mj.community.controller;

import cn.mj.community.pojo.Message;
import cn.mj.community.pojo.Page;
import cn.mj.community.pojo.User;
import cn.mj.community.service.MessageService;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.HostHolder;
import cn.mj.community.util.SensitiveFilter;
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
@RequestMapping(path = "/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    private User getTargetUser(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==id0){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }
    private List<Integer> getIds(List<Message> messages){
        List<Integer> ids =new ArrayList<>();
        if(messages!=null){
            for(Message message :messages){
                if(message.getToId() == hostHolder.getUser().getId() && message.getStatus() ==0){
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }
    @RequestMapping(path = "/list",method = RequestMethod.GET)
    public String getMessageList(Model model, Page page){
        User user = hostHolder.getUser();
        //page
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setPath("/message/list");
        //
        List<Message> conversations = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversationsVo = new ArrayList<>();

        if(conversations != null){
            for(Message conversation: conversations){
                Map<String,Object> conversationVo = new HashMap<>();
                conversationVo.put("conversation", conversation);
                conversationVo.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                conversationVo.put("unreadLettersCount",messageService.findUnreadLetterCount(user.getId(), conversation.getConversationId()));
                int targetId = user.getId()==conversation.getFromId()?conversation.getToId():conversation.getFromId();
                User target = userService.findUserById(targetId);
                conversationVo.put("target", target);

                conversationsVo.add(conversationVo);
            }
        }
        model.addAttribute("conversations",conversationsVo);
        model.addAttribute("unreadConversationCount",messageService.findUnreadLetterCount(user.getId(), null));
        return "/site/letter";
    }

    @RequestMapping(path ="/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId, Model model, Page page){

        //page
        page.setLimit(5);
        page.setPath("/message/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message: letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser",userService.findUserById(message.getFromId()));

                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        model.addAttribute("target",getTargetUser(conversationId));
        // set letter read
        List<Integer> ids = getIds(letterList);
        if (!ids.isEmpty()) messageService.updateStatus(ids,1);
        return "/site/letter-detail";
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        Integer.valueOf("a");
        //1.
        User target = userService.findUserByUserName(toName);
        if(target == null){
            return CommunityUtil.getJsonString(1,"the target user is empty");
        }
        //2.
        content = HtmlUtils.htmlEscape(content);
        content = sensitiveFilter.sensitiveWordsFilter(content);
        //3.
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setToId(target.getId());
        message.setStatus(0);
        message.setFromId(hostHolder.getUser().getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        messageService.sendMessage(message);
        return CommunityUtil.getJsonString(0,"send letter successfully");
    }
}
