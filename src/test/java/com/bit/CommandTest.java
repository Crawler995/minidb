package com.bit;

import com.bit.handler.CommandHandler;
import com.bit.handler.HandlerResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/23 3:24 下午
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandTest {

    @Autowired
    CommandHandler commandHandler;

    @Test
    public void commandTest() {
        String command = "select * from table1 left join table2 on table1.table1_id = table2.table2_friend_id";
        try {
            List<HandlerResult> handlerResult = commandHandler.handle(command);
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
