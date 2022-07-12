package cn.mj.community.service.impl;

import cn.mj.community.dao.LoginTicketMapper;
import cn.mj.community.dao.UserMapper;
import cn.mj.community.pojo.LoginTicket;
import cn.mj.community.pojo.User;
import cn.mj.community.service.UserService;
import cn.mj.community.util.CommunityConst;
import cn.mj.community.util.CommunityUtil;
import cn.mj.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService, CommunityConst {
    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.domain}")
    private String domain;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public User findUserById(int id) {
        // select user info in mysql -> select user info in redis
//        return userMapper.selectById(id);
        User user = findUserCache(id);
        if(user == null){
            user = initUserCache(id);
        }
        return user;
    }

    @Override
    public User findUserByUserName(String userName) {
        return userMapper.selectByUsername(userName);
    }

    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map=new HashMap<>();
        //1.
        if(user==null){
            logger.error("param is error, user is null");
            throw new IllegalArgumentException("param is error");
        }
        //2.check null
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","username is null");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","password is null");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","email is null");
            return map;
        }
        //3.check registered
        User u = userMapper.selectByUsername(user.getUsername());
        if (u!=null){
            map.put("usernameMsg","username was registered");
            return map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if (u!=null){
            map.put("emailMsg","email was registered");
            return map;
        }
        //4.register success
        user.setSalt(CommunityUtil.getUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        //http://images.nowcoder.com/head/149t.png
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        user.setActivationCode(CommunityUtil.getUUID());
        userMapper.insertUser(user);
        //5.send activationcode email
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("url",domain+contextPath+"/activate/"+user.getId()+"/"+user.getActivationCode());
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"ACTIVATE ACOUNT",content);
        return null;
    }

    @Override
    public Map<String, Object> login(String username,String password,int expired) {
        Map<String,Object> map =new HashMap<>();
        //2.
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","username is null");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","password is null");
            return map;
        }
        //3.
        User u = userMapper.selectByUsername(username);
        if(u==null){
            map.put("usernameMsg","username is wrong");
            return map;
        }
        if(!u.getPassword().equals(CommunityUtil.md5(password+u.getSalt()))){
            map.put("passwordMsg","password is wrong");
            return map;
        }
        //4.success login
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtil.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expired));
        loginTicket.setUserId(u.getId());
        //save loginTicket in mysql -> save loginTicket in redis
        //loginTicketMapper.insert(loginTicket);
        String ticketKey = CommunityUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;

    }

    @Override
    public int updateStatusById(int id, int status) {
        int re = userMapper.updateStatusById(id, status);
        clearUserCache(id);
        return re;
    }

    @Override
    public void loginOut(String ticket) {
//        loginTicketMapper.updateStatus(ticket,1);
        String ticketKey = CommunityUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    public LoginTicket findLoginTicketByTicket(String ticket){
        //select ticket in mysql ->select ticket in redis
//        return loginTicketMapper.selectLoginTicketByTicket(ticket);
        String ticketKey = CommunityUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    @Override
    public int updateHeaderUrlById(int id, String headerUrl) {
        int row = userMapper.updateHeaderUrlById(id, headerUrl);
        clearUserCache(id);
        return row;
    }

    @Override
    public int updatePassWordById(int id, String password) {
        int row = userMapper.updatePassWordById(id, password);
        clearUserCache(id);
        return row;
    }

    //1.select user info in redis
    //yes -> return
    //no  -> save user info in redis
    private User findUserCache(int userId){
        String userKey = CommunityUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    //2.save user info in redis
    private User initUserCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = CommunityUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,60*60, TimeUnit.SECONDS);
        return user;
    }
    //3.del user info in redis if updated the user info
    private void clearUserCache(int userId){
        String userKey = CommunityUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
