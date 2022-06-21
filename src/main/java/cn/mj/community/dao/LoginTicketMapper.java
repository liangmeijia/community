package cn.mj.community.dao;

import cn.mj.community.pojo.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert("insert into login_ticket (user_id,ticket,status,expired) values(#{userId},#{ticket},#{status},#{expired})")
    void insert(LoginTicket loginTicket);
    @Update("update login_ticket set status= #{status} where ticket= #{ticket}")
    void updateStatus(String ticket,int status);
    @Select("select id,user_id,ticket,status,expired from login_ticket where ticket = #{ticket}")
    LoginTicket selectLoginTicketByTicket(String ticket);
}
