package cn.mj.community.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter{
    private final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private TrieNode rootNode = new TrieNode();
    private static final String REPLACEMENT = "***";
    @PostConstruct
    private void init(){
        //init trie
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyWord;
            while((keyWord = reader.readLine())!=null){
                this.addKeyWord(keyWord);
            }
        }catch (IOException e){
            logger.error("open sensitive-words.txt error ");
        }
    }
    // add keyWord to trie
    private void addKeyWord(String keyWord){
        TrieNode tempNode = rootNode;
        for(int i = 0; i< keyWord.length(); i++){
            char c = keyWord.charAt(i);
            TrieNode childNode = tempNode.getChildNode(c);

            if(childNode == null){
                childNode = new TrieNode();
                tempNode.setChildNode(c, childNode);
            }

            tempNode = childNode;

            if(i == keyWord.length() - 1){
                tempNode.setSensitiveWordsFlag(true);
            }
        }
    }
    //core function
    public String sensitiveWordsFilter(String text){
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder ans = new StringBuilder();
        while (begin < text.length()){
            char c_begin = text.charAt(begin);
            if(isSymbol(c_begin)){
                begin++;
                position = begin;
                continue;
            }
            TrieNode childNode = tempNode.getChildNode(c_begin);

            while (childNode != null){
                //include text[begin]
                if(childNode.sensitiveWordsFlag){
                    ans.append(REPLACEMENT);
                    begin = position+1;
                    position = begin;
                    break;
                }
                position++;
                if(isSymbol(text.charAt(position))){
                    continue;
                }
                childNode = childNode.getChildNode(text.charAt(position));
            }
            if(childNode == null){
                //exclude text[begin]
                ans.append(text.charAt(begin));
                begin++;
                position = begin;

            }

        }
        return ans.toString();
    }
    //is special char (is true)
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);
    }
    //define TrieNode
    private class TrieNode{
      private boolean sensitiveWordsFlag = false;
      private Map<Character,TrieNode> childNodes = new HashMap<>();

      public void setChildNode(Character c, TrieNode trieNode){
          childNodes.put(c, trieNode);
      }
      public TrieNode getChildNode(Character c){
          return childNodes.get(c);
      }
      public void setSensitiveWordsFlag(boolean flag){
          sensitiveWordsFlag = flag;
      }
    }
}
