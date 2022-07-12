package cn.mj.community.service;

import cn.mj.community.pojo.Message;

import java.util.List;

public interface MessageService {
    List<Message> findConversations(int userId, int offset, int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findUnreadLetterCount(int userId, String conversationId);

    int sendMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    //
    //select least notice in the topic
    Message findLeastNotice(String topic, int userId);

    //select the count of notice (in the topic)
    int findNoticeCount(String topic, int userId);

    //select the count of unread notice (in the topic)
    int findUnReadNoticeCount(String topic, int userId);

    //select all notice list in the topic
    List<Message> findNotices(String topic, int userId, int offset, int limit);
}
