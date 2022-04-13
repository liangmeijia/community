package cn.mj.community.pojo;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;
@Data
@ToString
@Component
public class DiscussPost {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}