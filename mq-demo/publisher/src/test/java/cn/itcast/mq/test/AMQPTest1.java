package cn.itcast.mq.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AMQPTest1 {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public  void send1(){
        rabbitTemplate.convertAndSend("simple.qu111eue","yankaige");
    }

    @Test
    public  void send2() throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend("simple.queue","yankaige"+i);
            Thread.sleep(20);

        }
    }


    @Test
    public  void send3(){
        rabbitTemplate.convertAndSend("kai.fanout","","fanout消息");
    }

    @Test
    public  void send4(){


            rabbitTemplate.convertAndSend("kai.directEx","red","fanout消息");

    }

    @Test
    public  void send5(){

        Map<String, Object> map = new HashMap<>();
        map.put("name","kaige");
        map.put("age",12);
        rabbitTemplate.convertAndSend("kai.topicEx","a.news",map);

    }

}
