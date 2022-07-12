package cn.mj.community.dao;

import cn.mj.community.pojo.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //select all conversation(in fact ,select the newest letter in one conversation )
    List<Message> selectConversations(int userId, int offset, int limit);

    int selectConversationCount(int userId);

    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLetterCount(String conversationId);

    int unreadLetterCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    //system notice
    //select least notice in the topic
    Message selectLeastNotice(String topic, int userId);

    //select the count of notice (in the topic)
    int selectNoticeCount(String topic, int userId);

    //select the count of unread notice (in the topic)
    int selectUnReadNoticeCount(String topic, int userId);

    //select all notice list in the topic
    List<Message> selectNotices(String topic, int userId, int offset, int limit);
}
