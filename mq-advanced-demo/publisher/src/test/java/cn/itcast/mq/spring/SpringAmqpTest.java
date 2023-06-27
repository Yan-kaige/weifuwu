package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        rabbitTemplate.convertAndSend("amq.topic", "simple.66", "aa");
    }


    @Test
    public void testSendMessage2SimpleQueue1() throws InterruptedException {
        // 1.消息体
        String message = "hello, spring amqp!";
        // 2.全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 3.添加callback
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        log.debug("消息发送成功, ID:{}", correlationData.getId());
                    }else{
                        // 3.2.nack，消息失败
                        log.error("消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        // 4.发送消息
        rabbitTemplate.convertAndSend("amq.topic", "simple.66", message, correlationData);

        // 休眠一会儿，等待ack回执
        Thread.sleep(2000);
    }


    @Test
    public void ttl() throws InterruptedException {
        rabbitTemplate.convertAndSend("ttlEx", "ttl", "shiyua");
    }
    @Test
    public void delay() throws InterruptedException {

        Message build = MessageBuilder.withBody("delay 眼开个".getBytes(StandardCharsets.UTF_8)).setHeader("x-delay", 10000).build();
        rabbitTemplate.convertAndSend("delay.direct", "delay",build );
    }


    @Test
    public void lazy() throws InterruptedException {
        for (int i = 0; i < 1000000; i++) {
            rabbitTemplate.convertAndSend("lazyQ", "shiyua"+i);

        }
    }



}
