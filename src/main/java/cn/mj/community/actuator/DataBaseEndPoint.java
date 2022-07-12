package cn.mj.community.actuator;

import cn.mj.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DataBaseEndPoint {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseEndPoint.class);

    @Autowired
    private DataSource dataSource;

    @ReadOperation
    public String checkConnection(){
        try (Connection connection = dataSource.getConnection()) {
            return CommunityUtil.getJsonString(0,"get database connection successfully");
        }catch (SQLException e){
            logger.error("get database connection unsuccessfully");
            return CommunityUtil.getJsonString(1,"get database connection unsuccessfully");
        }
    }

}
