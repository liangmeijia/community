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
}
