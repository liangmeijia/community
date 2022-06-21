package cn.mj.community.dao;
import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
public class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for(Message message: messages){
            System.out.println(message);
        }
        int conversationCount = messageMapper.selectConversationCount(111);
        System.out.println(conversationCount);

        System.out.println("------------------------");

        List<Message> letters = messageMapper.selectLetters("111_113", 0, 20);
        for(Message letter: letters){
            System.out.println(letter);
        }
        int letterCount = messageMapper.selectLetterCount("111_113");
        System.out.println(letterCount);

        int unreadLetterCount = messageMapper.unreadLetterCount(111, null);
        System.out.println(unreadLetterCount);
        int unreadLetterCount1 = messageMapper.unreadLetterCount(111, "111_131");
        System.out.println(unreadLetterCount1);

    }
}
