package cn.mj.community.service.impl;

import cn.mj.community.dao.MessageMapper;
import cn.mj.community.pojo.Message;
import cn.mj.community.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findUnreadLetterCount(int userId, String conversationId) {
        return messageMapper.unreadLetterCount(userId,conversationId);
    }

    @Override
    public int sendMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    @Override
    public int updateStatus(List<Integer> ids, int status) {
        return messageMapper.updateStatus(ids,status);
    }
}
